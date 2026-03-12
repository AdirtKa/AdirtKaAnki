package com.example.adirtkaanki.carddetail

import com.example.adirtkaanki.data.model.DeckCard

data class CardDetailUiState(
    val isLoading: Boolean = false,
    val card: DeckCard? = null,
    val errorMessage: String? = null
)
