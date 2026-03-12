package com.example.adirtkaanki.data.model

data class DeckCard(
    val id: String,
    val deckId: String,
    val frontMainText: String,
    val frontSubText: String?,
    val backMainText: String,
    val backSubText: String?,
    val frontImageId: String?,
    val frontAudioId: String?,
    val backImageId: String?,
    val backAudioId: String?,
    val frontImageUrl: String?,
    val frontAudioUrl: String?,
    val backImageUrl: String?,
    val backAudioUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)
