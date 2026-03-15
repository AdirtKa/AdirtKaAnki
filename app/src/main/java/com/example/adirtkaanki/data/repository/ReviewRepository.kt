package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.mappers.toDomain
import com.example.adirtkaanki.data.model.CardProgress
import com.example.adirtkaanki.data.model.ReviewCard
import com.example.adirtkaanki.data.remote.ReviewApiService
import com.example.adirtkaanki.data.remote.dto.ReviewAnswerRequest

class ReviewRepository(
    private val api: ReviewApiService
) {
    suspend fun getDueCards(deckId: String, limit: Int = 50, offset: Int = 0): Result<List<ReviewCard>> {
        return try {
            val response = api.getDueCards(deckId = deckId, limit = limit, offset = offset)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun answerCard(cardId: String, quality: Int): Result<CardProgress> {
        return try {
            val response = api.answerCard(cardId = cardId, request = ReviewAnswerRequest(quality = quality))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
