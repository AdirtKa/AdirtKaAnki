package com.example.adirtkaanki.deckcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.DecksRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch

class DeckCardsViewModel(
    private val deckId: String,
    deckName: String,
    sessionManager: SessionManager
) : ViewModel() {

    private val decksApi = ApiFactory.createDecksApiService(sessionManager)
    private val decksRepository = DecksRepository(decksApi)

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

            val result = decksRepository.getDeckCards(deckId)

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

    fun refresh() {
        loadCards(showLoading = false)
    }
}
