package com.example.syncenginedashboard.ui.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SyncDashboardScreen(
    vm: SyncDashboardViewModel = viewModel()
) {
    val state = vm.uiState.collectAsStateWithLifecycle().value
    when (state) {
        SyncDashboardUiState.Loading -> Text("Loading...")
        is SyncDashboardUiState.Content -> Text(state.title)
    }
}