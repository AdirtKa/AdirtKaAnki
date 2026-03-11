package com.example.adirtkaanki.deckcards

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.data.model.DeckCard

@Composable
fun DeckCardsScreen(
    deckId: String,
    deckName: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DeckCardsViewModel = viewModel(
        factory = DeckCardsViewModelFactory(
            context = context,
            deckId = deckId,
            deckName = deckName
        )
    )

    DeckCardsScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeckCardsScreenPreview() {
    DeckCardsScreenContent(
        uiState = DeckCardsUiState(
            deckName = "English words",
            cards = listOf(
                DeckCard(id = "1", front = "apple", back = "€блоко"),
                DeckCard(id = "2", front = "book", back = "книга")
            )
        ),
        onBackClick = {},
        onRefresh = {}
    )
}
