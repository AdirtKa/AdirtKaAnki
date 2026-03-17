package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserStatsOverviewDto(
    @SerializedName("deck_count") val deckCount: Int,
    @SerializedName("card_count") val cardCount: Int,
    @SerializedName("reviewed_cards") val reviewedCards: Int,
    @SerializedName("new_cards") val newCards: Int,
    @SerializedName("learning_cards") val learningCards: Int,
    @SerializedName("mastered_cards") val masteredCards: Int,
    @SerializedName("due_now") val dueNow: Int,
    @SerializedName("correct_cards") val correctCards: Int,
    @SerializedName("incorrect_cards") val incorrectCards: Int
)

data class ReviewActivityPointDto(
    val date: String,
    val total: Int,
    val correct: Int,
    val incorrect: Int
)

data class ReviewHistoryPointDto(
    val date: String,
    @SerializedName("total_reviews") val totalReviews: Int,
    @SerializedName("correct_reviews") val correctReviews: Int,
    @SerializedName("incorrect_reviews") val incorrectReviews: Int,
    @SerializedName("average_quality") val averageQuality: Float? = null
)

data class DueForecastPointDto(
    val date: String,
    @SerializedName("due_cards") val dueCards: Int
)

data class DeckProgressStatsDto(
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("deck_name") val deckName: String,
    @SerializedName("total_cards") val totalCards: Int,
    @SerializedName("new_cards") val newCards: Int,
    @SerializedName("learning_cards") val learningCards: Int,
    @SerializedName("mastered_cards") val masteredCards: Int,
    @SerializedName("due_cards") val dueCards: Int
)
