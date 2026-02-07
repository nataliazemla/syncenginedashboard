package com.example.syncenginedashboard.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SyncRepository {
    val sourceStates: StateFlow<Map<SyncSource, SyncSourceState>>
    val logs: SharedFlow<LogEvent>

    fun run(source: SyncSource): Job
    fun cancel(source: SyncSource)
}