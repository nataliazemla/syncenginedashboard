package com.example.syncenginedashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.syncenginedashboard.ui.theme.SyncEngineDashboardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SyncDashboardUiState {
    data object Loading: SyncDashboardUiState
    data class Content(val title: String): SyncDashboardUiState
}

class SyncDashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SyncDashboardUiState>(
        SyncDashboardUiState.Content(title = "SyncLab")
    )
    val uiState: StateFlow<SyncDashboardUiState> = _uiState.asStateFlow()
}