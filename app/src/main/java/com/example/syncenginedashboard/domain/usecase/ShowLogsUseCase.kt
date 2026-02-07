package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.LogEvent
import com.example.syncenginedashboard.domain.SyncEngine
import kotlinx.coroutines.flow.Flow

class ShowLogsUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke(): Flow<LogEvent> = engine.logs
}