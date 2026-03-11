package com.example.adirtkaanki.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.repository.DecksRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DecksViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val authApi = ApiFactory.createAuthApiService(sessionManager)
    private val decksApi = ApiFactory.createDecksApiService(sessionManager)

    private val authRepository = AuthRepository(Database(), authApi, sessionManager)
    private val decksRepository = DecksRepository(decksApi)

    var uiState by mutableStateOf(DecksUiState())
        private set

    var logoutSuccess by mutableStateOf(false)
        private set

    init {
        loadMe()
        loadDecks()
    }

    fun onLogout() {
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            sessionManager.clearSession()
            logoutSuccess = true
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun loadMe() {
        viewModelScope.launch {
            val result = authRepository.getMe()
            val me = result.getOrNull()

            if (me != null) {
                uiState = uiState.copy(username = me.username)
            } else {
                uiState = uiState.copy(
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Не удалось загрузить пользователя"
                )
            }
        }
    }

    fun loadDecks() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = decksRepository.getDecks()

            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    decks = result.getOrDefault(emptyList())
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Не удалось загрузить колоды"
                )
            }
        }
    }

    fun createDeck(name: String, onSuccess: () -> Unit = {}) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isCreating = true, errorMessage = null)

            val result = decksRepository.createDeck(trimmedName)

            uiState = if (result.isSuccess) {
                val createdDeck = result.getOrNull()

                uiState.copy(
                    isCreating = false,
                    decks = if (createdDeck != null) uiState.decks + createdDeck else uiState.decks
                )
            } else {
                uiState.copy(
                    isCreating = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Не удалось создать колоду"
                )
            }

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            val result = decksRepository.deleteDeck(deck.id)

            if (result.isSuccess) {
                uiState = uiState.copy(
                    decks = uiState.decks.filter({ it.id != deck.id }),
                    errorMessage = null
                )
            } else {
                uiState = uiState.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Не удалосьб удалить колоду"
                )
            }
        }

    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun onLogoutNavigated() {
        logoutSuccess = false
    }
}