package com.example.adirtkaanki.register

import android.provider.Contacts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.AuthRepository
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.LoginResult
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val repository = AuthRepository(Database())
    var registerSuccess by mutableStateOf(false)
        private set

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value)
    }

    fun onSubmit() {
        uiState = uiState.copy(isLoading = true)

        if (
            uiState.username.isBlank() ||
            uiState.password.isBlank() ||
            uiState.confirmPassword.isBlank()
        ) {
            uiState = uiState.copy(errorMessage = "Заполните все поля")
            uiState.copy(isLoading = false)
            return
        }

        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Пароли не совпадают")
            uiState.copy(isLoading = false)
            return
        }

        viewModelScope.launch {
            delay(2000)
            when (val result = repository.register(uiState.username, uiState.password)) {
                is LoginResult.Success -> {
                    sessionManager.saveSession(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )

                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = null
                    )

                    registerSuccess = true
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
        registerSuccess = false
    }
}