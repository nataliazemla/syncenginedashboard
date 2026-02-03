package com.example.syncenginedashboard.domain

sealed interface SyncError {
    data object Network : SyncError
    data object Timeout : SyncError
    data object Unknown : SyncError
}