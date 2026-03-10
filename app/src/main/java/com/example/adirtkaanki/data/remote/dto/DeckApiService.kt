package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CreateDeckRequest
import com.example.adirtkaanki.data.remote.dto.DeckDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DecksApiService {

    @GET("decks")
    suspend fun getDecks(): List<DeckDto>

    @POST("decks/create")
    suspend fun createDeck(
        @Body request: CreateDeckRequest
    ): DeckDto
}