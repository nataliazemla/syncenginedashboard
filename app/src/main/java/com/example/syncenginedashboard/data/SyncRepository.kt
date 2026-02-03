package com.example.syncenginedashboard.data

import com.example.syncenginedashboard.domain.LogEvent
import com.example.syncenginedashboard.domain.SourceSyncState
import com.example.syncenginedashboard.domain.SyncSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SyncRepository {
    val sourceStates: StateFlow<Map<SyncSource, SourceSyncState>>
    val logs: SharedFlow<LogEvent>

    fun run(source: SyncSource): Job
    fun cancel(source: SyncSource)
}