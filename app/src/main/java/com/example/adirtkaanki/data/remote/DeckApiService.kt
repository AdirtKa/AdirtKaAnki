package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CardDto
import com.example.adirtkaanki.data.remote.dto.CreateDeckRequest
import com.example.adirtkaanki.data.remote.dto.DeckDto
import com.example.adirtkaanki.data.remote.dto.RenameDeckRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DecksApiService {

    @GET("decks")
    suspend fun getDecks(): List<DeckDto>

    @POST("decks/create")
    suspend fun createDeck(
        @Body request: CreateDeckRequest
    ): DeckDto

    @PUT("decks/{id}")
    suspend fun renameDeck(
        @Path("id") id: String,
        @Body request: RenameDeckRequest
    ): DeckDto

    @GET("decks/{id}/cards")
    suspend fun getDeckCards(
        @Path("id") id: String
    ): List<CardDto>

    @DELETE("decks/{id}")
    suspend fun deleteDeck(
        @Path("id") id: String
    )
}
