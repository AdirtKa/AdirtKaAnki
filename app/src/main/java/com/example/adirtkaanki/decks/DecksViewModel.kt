package com.example.adirtkaanki.decks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.AuthRepository
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DecksViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val repository = AuthRepository(Database())

    var uiState by mutableStateOf(DecksUiState())
        private set

    var logoutSuccess by mutableStateOf(false)
        private set

    fun onLogout() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            delay(1000)
            sessionManager.clearSession()
            logoutSuccess = true
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun onLogoutNavigated() {
        logoutSuccess = false
    }
}