package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.CardProgress
import com.example.adirtkaanki.data.model.ReviewCard
import com.example.adirtkaanki.data.remote.dto.CardProgressDto
import com.example.adirtkaanki.data.remote.dto.ReviewCardDto

fun CardProgressDto.toDomain(): CardProgress {
    return CardProgress(
        ease = ease,
        repetitions = repetitions,
        intervalDays = intervalDays,
        lastAnsweredAt = lastAnsweredAt,
        lastAnswerCorrect = lastAnswerCorrect,
        dueAt = dueAt
    )
}

fun ReviewCardDto.toDomain(): ReviewCard {
    return ReviewCard(
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
        progress = progress.toDomain()
    )
}
