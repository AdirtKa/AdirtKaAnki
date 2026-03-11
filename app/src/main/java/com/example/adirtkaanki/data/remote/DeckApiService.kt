package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CreateDeckRequest
import com.example.adirtkaanki.data.remote.dto.DeckDto
import com.example.adirtkaanki.data.remote.dto.DeleteDeckRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DecksApiService {

    @GET("decks")
    suspend fun getDecks(): List<DeckDto>

    @POST("decks/create")
    suspend fun createDeck(
        @Body request: CreateDeckRequest
    ): DeckDto

    @DELETE("decks/{id}")
    suspend fun deleteDeck(
        @Path("id") id: String
    ): Unit
}