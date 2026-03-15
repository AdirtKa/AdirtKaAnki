package com.example.adirtkaanki.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.ReviewRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val deckId: String,
    deckName: String,
    sessionManager: SessionManager
) : ViewModel() {

    private val reviewApi = ApiFactory.createReviewApiService(sessionManager)
    private val reviewRepository = ReviewRepository(reviewApi)

    var uiState by mutableStateOf(ReviewUiState(deckName = deckName))
        private set

    init {
        loadDueCards()
    }

    fun loadDueCards(showLoading: Boolean = true) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = if (showLoading) true else uiState.isLoading,
                isRefreshing = !showLoading,
                errorMessage = null
            )

            val result = reviewRepository.getDueCards(deckId = deckId)

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
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load review cards"
                )
            }
        }
    }

    fun answerCard(cardId: String, quality: Int) {
        if (uiState.isAnswering) return

        viewModelScope.launch {
            uiState = uiState.copy(isAnswering = true, errorMessage = null)

            val result = reviewRepository.answerCard(cardId = cardId, quality = quality)

            if (result.isSuccess) {
                uiState = uiState.copy(isAnswering = false)
                loadDueCards(showLoading = false)
            } else {
                uiState = uiState.copy(
                    isAnswering = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to submit answer"
                )
            }
        }
    }

    fun refresh() {
        loadDueCards(showLoading = false)
    }
}
