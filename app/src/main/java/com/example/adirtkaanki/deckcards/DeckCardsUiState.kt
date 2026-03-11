package com.example.adirtkaanki.deckcards

import com.example.adirtkaanki.data.model.DeckCard

data class DeckCardsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val deckName: String = "",
    val cards: List<DeckCard> = emptyList(),
    val errorMessage: String? = null
)
