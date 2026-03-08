package com.example.adirtkaanki.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.AuthRepository
import com.example.adirtkaanki.data.LoginResult
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val repository = AuthRepository(Database())

    var uiState by mutableStateOf(LoginUiState())
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onSubmit() {
        uiState = uiState.copy(isLoading = true)
        if (
            uiState.username.isBlank() ||
            uiState.password.isBlank()
        ) {
            uiState = uiState.copy(errorMessage = "Заполните все поля")
           uiState = uiState.copy(isLoading = false)
            return
        }

        viewModelScope.launch {
            delay(2000)
            when (val result = repository.login(uiState.username, uiState.password)) {
                is LoginResult.Success -> {
                    sessionManager.saveSession(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )

                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = null
                    )

                    loginSuccess = true
                }

                is LoginResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun onNavigated() {
        loginSuccess = false
    }
}