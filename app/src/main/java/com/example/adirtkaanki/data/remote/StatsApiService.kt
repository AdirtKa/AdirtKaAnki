package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.DeckProgressStatsDto
import com.example.adirtkaanki.data.remote.dto.DueForecastPointDto
import com.example.adirtkaanki.data.remote.dto.ReviewActivityPointDto
import com.example.adirtkaanki.data.remote.dto.ReviewHistoryPointDto
import com.example.adirtkaanki.data.remote.dto.UserStatsOverviewDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsApiService {

    @GET("stats/overview")
    suspend fun getOverview(
        @Query("deck_id") deckId: String? = null
    ): UserStatsOverviewDto

    @GET("stats/last-review-activity")
    suspend fun getLastReviewActivity(
        @Query("deck_id") deckId: String? = null,
        @Query("days") days: Int = 30
    ): List<ReviewActivityPointDto>

    @GET("stats/review-history")
    suspend fun getReviewHistory(
        @Query("deck_id") deckId: String? = null,
        @Query("days") days: Int = 30
    ): List<ReviewHistoryPointDto>

    @GET("stats/due-forecast")
    suspend fun getDueForecast(
        @Query("deck_id") deckId: String? = null,
        @Query("days") days: Int = 14
    ): List<DueForecastPointDto>

    @GET("stats/decks/progress")
    suspend fun getDecksProgress(): List<DeckProgressStatsDto>
}