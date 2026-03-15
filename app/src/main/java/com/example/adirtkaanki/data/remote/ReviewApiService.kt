package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CardProgressDto
import com.example.adirtkaanki.data.remote.dto.ReviewAnswerRequest
import com.example.adirtkaanki.data.remote.dto.ReviewCardDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApiService {

    @GET("review/due")
    suspend fun getDueCards(
        @Query("deck_id") deckId: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ReviewCardDto>

    @POST("review/cards/{cardId}/answer")
    suspend fun answerCard(
        @Path("cardId") cardId: String,
        @Body request: ReviewAnswerRequest
    ): CardProgressDto
}
