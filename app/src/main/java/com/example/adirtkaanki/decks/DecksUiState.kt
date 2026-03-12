package com.example.adirtkaanki.decks

import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckCardStats

data class DecksUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isCreating: Boolean = false,
    val isRenaming: Boolean = false,
    val username: String = "Unknown",
    val decks: List<Deck> = emptyList(),
    val deckStats: Map<String, DeckCardStats> = emptyMap(),
    val errorMessage: String? = null
)
