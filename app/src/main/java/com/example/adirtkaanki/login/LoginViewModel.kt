package com.example.adirtkaanki.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository(Database())

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onSubmit() {
        if (
            uiState.username.isBlank() ||
            uiState.password.isBlank()
        ) {
            uiState = uiState.copy(errorMessage = "Заполните все поля")
        }

        uiState = uiState.copy(isLoading = true)

        val success = repository.login(
            username = uiState.username,
            password = uiState.password
        )

        uiState = uiState.copy(isLoading = false)

        uiState = if (success) {
            uiState.copy(
                errorMessage = null,
                isLoggedIn = true
            )
        } else {
            uiState.copy(errorMessage = "Неверный логин или пароль")
        }
    }

    fun onNavigated() {
        uiState = uiState.copy(isLoggedIn = false)
    }
}