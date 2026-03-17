package com.example.adirtkaanki.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.repository.DecksRepository
import com.example.adirtkaanki.data.repository.StatsRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StatsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val authRepository = AuthRepository(ApiFactory.createAuthApiService(sessionManager), sessionManager)
    private val decksRepository = DecksRepository(ApiFactory.createDecksApiService(sessionManager))
    private val statsRepository = StatsRepository(ApiFactory.createStatsApiService(sessionManager))

    var uiState by mutableStateOf(StatsUiState())
        private set

    init {
        loadInitialData()
    }

    fun loadInitialData(showLoading: Boolean = true) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = if (showLoading) true else uiState.isLoading,
                isRefreshing = !showLoading,
                errorMessage = null
            )

            val meDeferred = async { authRepository.getMe() }
            val decksDeferred = async { decksRepository.getDecks() }

            val meResult = meDeferred.await()
            val decksResult = decksDeferred.await()
            val decks = decksResult.getOrDefault(emptyList())

            uiState = uiState.copy(
                username = meResult.getOrNull()?.username ?: uiState.username,
                availableDecks = decks
            )

            loadStatsForDeck(deckId = uiState.selectedDeckId, decks = decks, showLoading = showLoading)
        }
    }

    fun selectDeck(deck: Deck?) {
        uiState = uiState.copy(
            selectedDeckId = deck?.id,
            selectedDeckName = deck?.name
        )
        loadStatsForDeck(deckId = deck?.id, decks = uiState.availableDecks, showLoading = true)
    }

    fun refresh() {
        loadStatsForDeck(deckId = uiState.selectedDeckId, decks = uiState.availableDecks, showLoading = false)
    }

    private fun loadStatsForDeck(deckId: String?, decks: List<Deck>, showLoading: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = if (showLoading) true else uiState.isLoading,
                isRefreshing = !showLoading,
                errorMessage = null,
                availableDecks = decks
            )

            val overviewDeferred = async { statsRepository.getOverview(deckId) }
            val activityDeferred = async { statsRepository.getLastReviewActivity(deckId = deckId, days = 30) }
            val historyDeferred = async { statsRepository.getReviewHistory(deckId = deckId, days = 30) }
            val forecastDeferred = async { statsRepository.getDueForecast(deckId = deckId, days = 14) }
            val deckProgressDeferred = async { statsRepository.getDecksProgress() }

            val overviewResult = overviewDeferred.await()
            val activityResult = activityDeferred.await()
            val historyResult = historyDeferred.await()
            val forecastResult = forecastDeferred.await()
            val deckProgressResult = deckProgressDeferred.await()

            val firstFailure = listOf(
                overviewResult.exceptionOrNull(),
                activityResult.exceptionOrNull(),
                historyResult.exceptionOrNull(),
                forecastResult.exceptionOrNull(),
                deckProgressResult.exceptionOrNull()
            ).firstOrNull { it != null }

            uiState = uiState.copy(
                isLoading = false,
                isRefreshing = false,
                overview = overviewResult.getOrNull(),
                lastReviewActivity = activityResult.getOrDefault(emptyList()),
                reviewHistory = historyResult.getOrDefault(emptyList()),
                dueForecast = forecastResult.getOrDefault(emptyList()),
                decksProgress = deckProgressResult.getOrDefault(emptyList()),
                errorMessage = firstFailure?.message
            )
        }
    }
}
