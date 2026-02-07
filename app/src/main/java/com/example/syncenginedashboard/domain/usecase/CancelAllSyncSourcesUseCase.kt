package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.SyncEngine
import com.example.syncenginedashboard.domain.SyncSource

class CancelAllSyncSourcesUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke() = SyncSource.entries.forEach(engine::cancel)
}