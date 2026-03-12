package com.example.adirtkaanki.deckcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.model.DeckCard
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.CardsRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch

class DeckCardsViewModel(
    private val deckId: String,
    deckName: String,
    sessionManager: SessionManager
) : ViewModel() {

    private val cardsApi = ApiFactory.createCardsApiService(sessionManager)
    private val cardsRepository = CardsRepository(cardsApi)

    var uiState by mutableStateOf(DeckCardsUiState(deckName = deckName))
        private set

    init {
        loadCards()
    }

    fun loadCards(showLoading: Boolean = true) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = if (showLoading) true else uiState.isLoading,
                isRefreshing = !showLoading,
                errorMessage = null
            )

            val result = cardsRepository.listCards(deckId = deckId)

            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    cards = result.getOrDefault(emptyList())
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load cards"
                )
            }
        }
    }

    fun deleteCard(card: DeckCard) {
        viewModelScope.launch {
            uiState = uiState.copy(isDeleting = true, errorMessage = null)

            val result = cardsRepository.deleteCard(card.id)

            uiState = if (result.isSuccess) {
                uiState.copy(
                    isDeleting = false,
                    cards = uiState.cards.filter { it.id != card.id }
                )
            } else {
                uiState.copy(
                    isDeleting = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to delete card"
                )
            }
        }
    }

    fun refresh() {
        loadCards(showLoading = false)
    }
}
