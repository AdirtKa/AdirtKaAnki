package com.example.adirtkaanki.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DecksViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val api = ApiFactory.createAuthApiService(sessionManager)
    private val repository = AuthRepository(Database(), api, sessionManager)

    var uiState by mutableStateOf(DecksUiState())
        private set

    var logoutSuccess by mutableStateOf(false)
        private set

    init {
        loadMe()
    }

    fun onLogout() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            delay(1000)
            sessionManager.clearSession()
            logoutSuccess = true
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun loadMe() {
        viewModelScope.launch {
            val result = repository.getMe()
            val me = result.getOrNull()
            if (me != null) {
                uiState = uiState.copy(username = me.username)
            } else {
                println(result.exceptionOrNull()?.message)
            }
        }
    }

    fun onLogoutNavigated() {
        logoutSuccess = false
    }
}