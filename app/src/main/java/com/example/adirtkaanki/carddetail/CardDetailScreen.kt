package com.example.adirtkaanki.carddetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CardDetailScreen(
    cardId: String,
    refreshSignal: StateFlow<Boolean>,
    onRefreshHandled: () -> Unit,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CardDetailViewModel = viewModel(
        factory = CardDetailViewModelFactory(
            context = context,
            cardId = cardId
        )
    )
    val shouldRefresh by refreshSignal.collectAsState()

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadCard()
        }
    }

    CardDetailScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onEditClick = onEditClick
    )
}
