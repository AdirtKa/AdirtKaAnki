package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.mappers.toDomain
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckCardStats
import com.example.adirtkaanki.data.remote.DecksApiService
import com.example.adirtkaanki.data.remote.dto.CreateDeckRequest
import com.example.adirtkaanki.data.remote.dto.RenameDeckRequest

class DecksRepository(
    private val api: DecksApiService
) {
    suspend fun getDecks(): Result<List<Deck>> {
        return try {
            val response = api.getDecks()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDeck(name: String): Result<Deck> {
        return try {
            val response = api.createDeck(CreateDeckRequest(name = name))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameDeck(deckId: String, name: String): Result<Deck> {
        return try {
            val response = api.renameDeck(deckId, RenameDeckRequest(name = name))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDeck(deckId: String): Result<Unit> {
        return try {
            api.deleteDeck(deckId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeckStats(deckId: String): Result<DeckCardStats> {
        return try {
            val response = api.getDeckStats(deckId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
