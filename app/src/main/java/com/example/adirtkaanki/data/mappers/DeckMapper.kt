package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.remote.dto.DeckDto

fun DeckDto.toDomain(): Deck {
    return Deck(
        id = id,
        name = name
    )
}