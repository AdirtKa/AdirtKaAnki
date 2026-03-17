package com.example.adirtkaanki.data.mappers

import com.example.adirtkaanki.data.model.DeckProgressStats
import com.example.adirtkaanki.data.model.DueForecastPoint
import com.example.adirtkaanki.data.model.ReviewActivityPoint
import com.example.adirtkaanki.data.model.ReviewHistoryPoint
import com.example.adirtkaanki.data.model.UserStatsOverview
import com.example.adirtkaanki.data.remote.dto.DeckProgressStatsDto
import com.example.adirtkaanki.data.remote.dto.DueForecastPointDto
import com.example.adirtkaanki.data.remote.dto.ReviewActivityPointDto
import com.example.adirtkaanki.data.remote.dto.ReviewHistoryPointDto
import com.example.adirtkaanki.data.remote.dto.UserStatsOverviewDto

fun UserStatsOverviewDto.toDomain(): UserStatsOverview {
    return UserStatsOverview(
        deckCount = deckCount,
        cardCount = cardCount,
        reviewedCards = reviewedCards,
        newCards = newCards,
        learningCards = learningCards,
        masteredCards = masteredCards,
        dueNow = dueNow,
        correctCards = correctCards,
        incorrectCards = incorrectCards
    )
}

fun ReviewActivityPointDto.toDomain(): ReviewActivityPoint {
    return ReviewActivityPoint(
        date = date,
        total = total,
        correct = correct,
        incorrect = incorrect
    )
}

fun ReviewHistoryPointDto.toDomain(): ReviewHistoryPoint {
    return ReviewHistoryPoint(
        date = date,
        totalReviews = totalReviews,
        correctReviews = correctReviews,
        incorrectReviews = incorrectReviews,
        averageQuality = averageQuality
    )
}

fun DueForecastPointDto.toDomain(): DueForecastPoint {
    return DueForecastPoint(
        date = date,
        dueCards = dueCards
    )
}

fun DeckProgressStatsDto.toDomain(): DeckProgressStats {
    return DeckProgressStats(
        deckId = deckId,
        deckName = deckName,
        totalCards = totalCards,
        newCards = newCards,
        learningCards = learningCards,
        masteredCards = masteredCards,
        dueCards = dueCards
    )
}
