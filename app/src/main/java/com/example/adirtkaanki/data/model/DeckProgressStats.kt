package com.example.adirtkaanki.data.model

data class DeckProgressStats(
    val deckId: String,
    val deckName: String,
    val totalCards: Int,
    val newCards: Int,
    val learningCards: Int,
    val masteredCards: Int,
    val dueCards: Int
)
