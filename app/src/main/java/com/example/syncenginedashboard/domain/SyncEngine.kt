package com.example.syncenginedashboard.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface SyncEngine {
    val sourceStates: Flow<Map<SyncSource, SyncSourceState>>
    val logs: Flow<LogEvent>

    fun run(source: SyncSource): Job
    fun cancel(source: SyncSource)
}