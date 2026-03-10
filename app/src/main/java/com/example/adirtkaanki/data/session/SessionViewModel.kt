package com.example.adirtkaanki.data.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedIn : SessionState
    data object LoggedOut : SessionState
}

class SessionViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    var sessionState by mutableStateOf<SessionState>(SessionState.Loading)
        private set

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            try {
                sessionManager.accessTokenFlow.collect { token ->
                    sessionState = if (token.isNullOrBlank()) {
                        SessionState.LoggedOut
                    } else {
                        SessionState.LoggedIn
                    }
                }
            } catch (e: Exception) {
                sessionState = SessionState.LoggedOut
            }
        }
    }
}