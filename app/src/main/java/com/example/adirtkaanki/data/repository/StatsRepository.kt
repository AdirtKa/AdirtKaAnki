package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.mappers.toDomain
import com.example.adirtkaanki.data.model.DeckProgressStats
import com.example.adirtkaanki.data.model.DueForecastPoint
import com.example.adirtkaanki.data.model.ReviewActivityPoint
import com.example.adirtkaanki.data.model.ReviewHistoryPoint
import com.example.adirtkaanki.data.model.UserStatsOverview
import com.example.adirtkaanki.data.remote.StatsApiService

class StatsRepository(
    private val api: StatsApiService
) {
    suspend fun getOverview(deckId: String? = null): Result<UserStatsOverview> {
        return try {
            Result.success(api.getOverview(deckId).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLastReviewActivity(deckId: String? = null, days: Int = 30): Result<List<ReviewActivityPoint>> {
        return try {
            Result.success(api.getLastReviewActivity(deckId = deckId, days = days).map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewHistory(deckId: String? = null, days: Int = 30): Result<List<ReviewHistoryPoint>> {
        return try {
            Result.success(api.getReviewHistory(deckId = deckId, days = days).map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDueForecast(deckId: String? = null, days: Int = 14): Result<List<DueForecastPoint>> {
        return try {
            Result.success(api.getDueForecast(deckId = deckId, days = days).map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDecksProgress(): Result<List<DeckProgressStats>> {
        return try {
            Result.success(api.getDecksProgress().map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
