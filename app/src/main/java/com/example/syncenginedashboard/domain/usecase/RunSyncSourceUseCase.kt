package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.SyncEngine
import com.example.syncenginedashboard.domain.SyncSource
import kotlinx.coroutines.Job

class RunSyncSourceUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke(source: SyncSource): Job = engine.run(source)
}