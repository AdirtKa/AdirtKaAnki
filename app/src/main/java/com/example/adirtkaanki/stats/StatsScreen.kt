package com.example.adirtkaanki.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StatsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(context))

    StatsScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onDeckSelected = viewModel::selectDeck
    )
}
