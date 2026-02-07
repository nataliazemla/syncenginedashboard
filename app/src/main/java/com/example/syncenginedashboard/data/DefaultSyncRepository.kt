package com.example.syncenginedashboard.data

import com.example.syncenginedashboard.core.dispatchers.DispatcherProvider
import com.example.syncenginedashboard.data.local.InMemoryLocalDataSource
import com.example.syncenginedashboard.data.remote.FakeRemoteDataSource
import com.example.syncenginedashboard.domain.LogEvent
import com.example.syncenginedashboard.domain.SyncSourceState
import com.example.syncenginedashboard.domain.SyncError
import com.example.syncenginedashboard.domain.SyncRepository
import com.example.syncenginedashboard.domain.SyncRunId
import com.example.syncenginedashboard.domain.SyncSource
import com.example.syncenginedashboard.domain.SyncStatus
import com.example.syncenginedashboard.domain.SyncStep
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class DefaultSyncRepository(
    private val dispatchers: DispatcherProvider,
    private val remote: FakeRemoteDataSource,
    private val local: InMemoryLocalDataSource,
    sources: List<SyncSource> = SyncSource.entries,
    private val nowMs: () -> Long = { System.currentTimeMillis() },
    private val runIdFactory: () -> SyncRunId = { SyncRunId(UUID.randomUUID().toString()) }
) : SyncRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    private val jobsMutex = Mutex()
    private val jobs: MutableMap<SyncSource, Job> = mutableMapOf()

    private val _sourceStates = MutableStateFlow(
        sources.associateWith { source -> SyncSourceState(source = source) }
    )
    override val sourceStates: StateFlow<Map<SyncSource, SyncSourceState>> =
        _sourceStates.asStateFlow()

    private val _logs = MutableSharedFlow<LogEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val logs = _logs.asSharedFlow()

    override fun run(source: SyncSource): Job {
        val job = scope.launch {
            val runId = runIdFactory()

            jobsMutex.withLock {
                jobs[source]?.cancel(CancellationException("Replaced by a new run"))
                jobs[source] = this.coroutineContext[Job]!!
            }

            try {
                setRunning(source, runId, step = SyncStep.Fetch, progress = 0.1f)
                log(source, LogEvent.Level.INFO, "Fetch started", runId)

                val items = withContext(dispatchers.io) {
                    remote.fetch(source)
                }

                setRunning(source, runId, step = SyncStep.Transform, progress = 0.6f)
                log(source, LogEvent.Level.INFO, "Transform started", runId)

                val transformed = withContext(dispatchers.default) {
                    items.map { it.uppercase() }
                }

                setRunning(source, runId, step = SyncStep.Persist, progress = 0.85f)
                log(source, LogEvent.Level.INFO, "Persist started", runId)

                withContext(dispatchers.io) {
                    local.save(source, transformed)
                }

                setSuccess(source, runId)
                log(source, LogEvent.Level.INFO, "Sync finished successfully", runId)
            } catch (ce: CancellationException) {
                setCancelled(source)
                log(source, LogEvent.Level.WARN, "Sync cancelled: ${ce.message ?: "no reason"}", runId = null)
                throw ce
            } catch (t: Throwable) {
                val mapped = t.toSyncError()
                setFailed(source, mapped)
                log(source, LogEvent.Level.ERROR, "Sync failed: ${t.message ?: t::class.java.simpleName}", runId = null)
            } finally {
                jobsMutex.withLock {
                    jobs.remove(source)
                }
            }
        }

        return job
    }

    override fun cancel(source: SyncSource) {
        scope.launch {
            jobsMutex.withLock {
                jobs[source]?.cancel(CancellationException("Cancelled by user"))
            }
        }
    }

    private fun setRunning(
        source: SyncSource,
        runId: SyncRunId,
        step: SyncStep,
        progress: Float
    ) {
        _sourceStates.update { map ->
            map + (source to map.getValue(source).copy(
                status = SyncStatus.Running,
                currentStep = step,
                progress = progress.coerceIn(0f, 1f),
                runId = runId,
                error = null
            ))
        }
    }

    private fun setSuccess(source: SyncSource, runId: SyncRunId) {
        _sourceStates.update { map ->
            map + (source to map.getValue(source).copy(
                status = SyncStatus.Success,
                currentStep = null,
                progress = 1f,
                runId = runId,
                error = null
            ))
        }
    }

    private fun setFailed(source: SyncSource, error: SyncError) {
        _sourceStates.update { map ->
            map + (source to map.getValue(source).copy(
                status = SyncStatus.Failed,
                currentStep = null,
                error = error
            ))
        }
    }

    private fun setCancelled(source: SyncSource) {
        _sourceStates.update { map ->
            map + (source to map.getValue(source).copy(
                status = SyncStatus.Cancelled,
                currentStep = null
            ))
        }
    }

    private fun log(
        source: SyncSource,
        level: LogEvent.Level,
        message: String,
        runId: SyncRunId?
    ) {
        _logs.tryEmit(
            LogEvent(
                timestampMs = nowMs(),
                source = source,
                level = level,
                message = message,
                runId = runId
            )
        )
    }

    private fun Throwable.toSyncError(): SyncError = when (this) {
        is IOException -> SyncError.Network
        else -> SyncError.Unknown
    }
}