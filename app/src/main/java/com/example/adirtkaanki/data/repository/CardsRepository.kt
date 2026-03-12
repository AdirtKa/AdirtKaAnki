package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.mappers.toDomain
import com.example.adirtkaanki.data.model.DeckCard
import com.example.adirtkaanki.data.remote.CardsApiService
import com.example.adirtkaanki.data.remote.dto.CardCreateRequest
import com.example.adirtkaanki.data.remote.dto.CardUpdateRequest

class CardsRepository(
    private val api: CardsApiService
) {
    suspend fun listCards(deckId: String, limit: Int = 200, offset: Int = 0): Result<List<DeckCard>> {
        return try {
            val response = api.listCards(deckId = deckId, limit = limit, offset = offset)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCard(
        deckId: String,
        frontMainText: String,
        frontSubText: String?,
        backMainText: String,
        backSubText: String?,
        frontImageId: String?,
        frontAudioId: String?,
        backImageId: String?,
        backAudioId: String?
    ): Result<DeckCard> {
        return try {
            val response = api.createCard(
                CardCreateRequest(
                    deckId = deckId,
                    frontMainText = frontMainText,
                    frontSubText = frontSubText,
                    backMainText = backMainText,
                    backSubText = backSubText,
                    frontImageId = frontImageId,
                    frontAudioId = frontAudioId,
                    backImageId = backImageId,
                    backAudioId = backAudioId
                )
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCard(
        cardId: String,
        frontMainText: String,
        frontSubText: String?,
        backMainText: String,
        backSubText: String?,
        frontImageId: String?,
        frontAudioId: String?,
        backImageId: String?,
        backAudioId: String?
    ): Result<DeckCard> {
        return try {
            val response = api.updateCard(
                cardId = cardId,
                request = CardUpdateRequest(
                    frontMainText = frontMainText,
                    frontSubText = frontSubText,
                    backMainText = backMainText,
                    backSubText = backSubText,
                    frontImageId = frontImageId,
                    frontAudioId = frontAudioId,
                    backImageId = backImageId,
                    backAudioId = backAudioId
                )
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCard(cardId: String): Result<DeckCard> {
        return try {
            val response = api.getCard(cardId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCard(cardId: String): Result<Unit> {
        return try {
            api.deleteCard(cardId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
