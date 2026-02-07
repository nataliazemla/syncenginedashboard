package com.example.syncenginedashboard.domain

data class SyncSourceState(
    val source: SyncSource,
    val status: SyncStatus = SyncStatus.Idle,
    val currentStep: SyncStep? = null,
    val progress: Float = 0f, // 0..1
    val runId: SyncRunId? = null,
    val error: SyncError? = null
)