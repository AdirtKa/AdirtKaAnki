package com.example.adirtkaanki.data.model

data class UserStatsOverview(
    val deckCount: Int,
    val cardCount: Int,
    val reviewedCards: Int,
    val newCards: Int,
    val learningCards: Int,
    val masteredCards: Int,
    val dueNow: Int,
    val correctCards: Int,
    val incorrectCards: Int
)
