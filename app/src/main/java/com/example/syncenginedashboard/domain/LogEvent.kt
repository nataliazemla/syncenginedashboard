package com.example.syncenginedashboard.domain

data class LogEvent(
    val timestampMs: Long,
    val source: SyncSource,
    val level: Level,
    val message: String,
    val runId: SyncRunId? = null
) {
    enum class Level { INFO, WARN, ERROR }
}