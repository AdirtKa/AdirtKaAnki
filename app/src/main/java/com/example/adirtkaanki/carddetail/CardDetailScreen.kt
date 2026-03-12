package com.example.adirtkaanki.carddetail

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.data.model.DeckCard

@Composable
fun CardDetailScreen(
    cardId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CardDetailViewModel = viewModel(
        factory = CardDetailViewModelFactory(
            context = context,
            cardId = cardId
        )
    )

    CardDetailScreenContent(
        uiState = viewModel.uiState,
        onBackClick = onBackClick
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CardDetailScreenPreview() {
    CardDetailScreenContent(
        uiState = CardDetailUiState(
            card = DeckCard(
                id = "1",
                deckId = "deck-1",
                frontMainText = "apple",
                frontSubText = "noun",
                backMainText = "€блоко",
                backSubText = "fruit",
                frontImageId = null,
                frontAudioId = null,
                backImageId = null,
                backAudioId = null,
                frontImageUrl = "/media/front-image",
                frontAudioUrl = null,
                backImageUrl = null,
                backAudioUrl = null,
                createdAt = null,
                updatedAt = null
            )
        ),
        onBackClick = {}
    )
}
