package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.DeckCard
import com.example.adirtkaanki.data.remote.dto.CardDto

fun CardDto.toDomain(): DeckCard {
    val resolvedFront = front ?: question ?: term ?: ""
    val resolvedBack = back ?: answer ?: definition ?: ""

    return DeckCard(
        id = id,
        front = resolvedFront,
        back = resolvedBack
    )
}
