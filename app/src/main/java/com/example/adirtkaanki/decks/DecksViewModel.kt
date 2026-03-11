package com.example.adirtkaanki.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.repository.DecksRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch

class DecksViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val authApi = ApiFactory.createAuthApiService(sessionManager)
    private val decksApi = ApiFactory.createDecksApiService(sessionManager)

    private val authRepository = AuthRepository(authApi, sessionManager)
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
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load user"
                )
            }
        }
    }

    fun loadDecks(showLoading: Boolean = true) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = if (showLoading) true else uiState.isLoading,
                isRefreshing = !showLoading,
                errorMessage = null
            )

            val result = decksRepository.getDecks()

            uiState = if (result.isSuccess) {
                uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    decks = result.getOrDefault(emptyList())
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load decks"
                )
            }
        }
    }

    fun refresh() {
        loadDecks(showLoading = false)
        loadMe()
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
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to create deck"
                )
            }

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun renameDeck(deck: Deck, newName: String, onSuccess: () -> Unit = {}) {
        val trimmedName = newName.trim()
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isRenaming = true, errorMessage = null)
            val result = decksRepository.renameDeck(deck.id, trimmedName)

            uiState = if (result.isSuccess) {
                val renamedDeck = result.getOrNull()
                uiState.copy(
                    isRenaming = false,
                    decks = uiState.decks.map {
                        if (it.id == deck.id && renamedDeck != null) renamedDeck else it
                    }
                )
            } else {
                uiState.copy(
                    isRenaming = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to rename deck"
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
                    decks = uiState.decks.filter { it.id != deck.id },
                    errorMessage = null
                )
            } else {
                uiState = uiState.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to delete deck"
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
