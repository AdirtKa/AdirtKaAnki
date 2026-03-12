package com.example.adirtkaanki.deckcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DeckCardsScreen(
    deckId: String,
    deckName: String,
    refreshSignal: StateFlow<Boolean>,
    onRefreshHandled: () -> Unit,
    onBackClick: () -> Unit,
    onCreateCardClick: () -> Unit,
    onCardClick: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: DeckCardsViewModel = viewModel(
        factory = DeckCardsViewModelFactory(
            context = context,
            deckId = deckId,
            deckName = deckName
        )
    )
    val shouldRefresh by refreshSignal.collectAsState()

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadCards()
            onRefreshHandled()
        }
    }

    DeckCardsScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onCreateCardClick = onCreateCardClick,
        onRefresh = viewModel::refresh,
        onCardClick = { card -> onCardClick(card.id) },
        onDeleteCardClick = viewModel::deleteCard
    )
}
