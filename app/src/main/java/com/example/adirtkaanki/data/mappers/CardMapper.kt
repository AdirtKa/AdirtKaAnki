package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.DeckCard
import com.example.adirtkaanki.data.remote.dto.CardDto

fun CardDto.toDomain(): DeckCard {
    return DeckCard(
        id = id,
        deckId = deckId,
        frontMainText = frontMainText,
        frontSubText = frontSubText,
        backMainText = backMainText,
        backSubText = backSubText,
        frontImageId = frontImageId,
        frontAudioId = frontAudioId,
        backImageId = backImageId,
        backAudioId = backAudioId,
        frontImageUrl = frontImageUrl,
        frontAudioUrl = frontAudioUrl,
        backImageUrl = backImageUrl,
        backAudioUrl = backAudioUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
