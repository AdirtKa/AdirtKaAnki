package com.example.adirtkaanki.data.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
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
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val token = sessionManager.accessTokenFlow.first()

                sessionState = if (token.isNullOrBlank()) {
                    SessionState.LoggedOut
                } else {
                    SessionState.LoggedIn
                }
            } catch (e: Exception) {
                sessionState = SessionState.LoggedOut
            }
        }
    }
}