package com.example.syncenginedashboard.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SyncDashboardScreen(
    vm: SyncDashboardViewModel = viewModel(),
    paddingValues: PaddingValues
) {
    val state = vm.uiState.collectAsStateWithLifecycle().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            SyncDashboardUiState.Loading -> Text("Loading...")
            is SyncDashboardUiState.Content -> Text(state.title)
        }
    }
}