package com.example.adirtkaanki.review

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReviewScreen(
    deckId: String,
    deckName: String,
    onBackClick: () -> Unit,
    onViewCardsClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModelFactory(
            context = context,
            deckId = deckId,
            deckName = deckName
        )
    )

    ReviewScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onViewCardsClick = onViewCardsClick,
        onRefresh = viewModel::refresh,
        onAnswer = viewModel::answerCard
    )
}
