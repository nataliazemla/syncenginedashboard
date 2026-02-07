package com.example.syncenginedashboard.domain.usecase

import com.example.syncenginedashboard.domain.SyncEngine
import com.example.syncenginedashboard.domain.SyncSource
import kotlinx.coroutines.Job

class RunAllSyncSourcesUseCase(
    private val engine: SyncEngine
) {
    operator fun invoke(): List<Job> = SyncSource.entries.map(engine::run)
}