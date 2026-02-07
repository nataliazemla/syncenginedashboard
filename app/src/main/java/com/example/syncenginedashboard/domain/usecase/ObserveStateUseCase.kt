package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.SyncEngine
import com.example.syncenginedashboard.domain.SyncSource
import com.example.syncenginedashboard.domain.SyncSourceState
import kotlinx.coroutines.flow.Flow

class ObserveStateUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke(): Flow<Map<SyncSource, SyncSourceState>> = engine.sourceStates
}