package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CardCreateRequest
import com.example.adirtkaanki.data.remote.dto.CardDto
import com.example.adirtkaanki.data.remote.dto.CardUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CardsApiService {

    @GET("cards/")
    suspend fun listCards(
        @Query("deck_id") deckId: String,
        @Query("limit") limit: Int = 200,
        @Query("offset") offset: Int = 0
    ): List<CardDto>

    @POST("cards/")
    suspend fun createCard(
        @Body request: CardCreateRequest
    ): CardDto

    @GET("cards/{cardId}")
    suspend fun getCard(
        @Path("cardId") cardId: String
    ): CardDto

    @PUT("cards/{cardId}")
    suspend fun updateCard(
        @Path("cardId") cardId: String,
        @Body request: CardUpdateRequest
    ): CardDto

    @DELETE("cards/{cardId}")
    suspend fun deleteCard(
        @Path("cardId") cardId: String
    )
}
