package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckCardStats
import com.example.adirtkaanki.data.remote.dto.DeckCardStatsDto
import com.example.adirtkaanki.data.remote.dto.DeckDto

fun DeckDto.toDomain(): Deck {
    return Deck(
        id = id,
        name = name
    )
}

fun DeckCardStatsDto.toDomain(): DeckCardStats {
    return DeckCardStats(
        notStudied = notStudied,
        answeredCorrectly = answeredCorrectly,
        answeredIncorrectly = answeredIncorrectly
    )
}
