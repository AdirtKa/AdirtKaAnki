package com.example.adirtkaanki.decks

import com.example.adirtkaanki.data.model.Deck

data class DecksUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val username: String = "Unknown",
    val decks: List<Deck> = emptyList(),
    val errorMessage: String? = null
)