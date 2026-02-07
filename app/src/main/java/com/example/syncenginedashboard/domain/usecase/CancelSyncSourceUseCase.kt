package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.SyncEngine
import com.example.syncenginedashboard.domain.SyncSource

class CancelSyncSourceUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke(source: SyncSource) = engine.cancel(source)
}