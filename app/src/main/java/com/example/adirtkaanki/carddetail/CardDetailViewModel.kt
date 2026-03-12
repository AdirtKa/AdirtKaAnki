package com.example.adirtkaanki.carddetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.CardsRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val cardId: String,
    sessionManager: SessionManager
) : ViewModel() {

    private val cardsApi = ApiFactory.createCardsApiService(sessionManager)
    private val cardsRepository = CardsRepository(cardsApi)

    var uiState by mutableStateOf(CardDetailUiState())
        private set

    init {
        loadCard()
    }

    fun loadCard() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = cardsRepository.getCard(cardId)

            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    card = result.getOrNull()
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load card"
                )
            }
        }
    }
}
