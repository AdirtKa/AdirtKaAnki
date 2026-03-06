package com.example.adirtkaanki.register

import android.provider.Contacts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.adirtkaanki.data.AuthRepository
import com.example.adirtkaanki.data.Database

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepository(Database())

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
        if (
            uiState.username.isBlank() ||
            uiState.password.isBlank() ||
            uiState.confirmPassword.isBlank()
        ) {
            uiState = uiState.copy(errorMessage = "Заполните все поля")
        }

        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Пароли не совпадают")
            return
        }

        val success = repository.register(
            uiState.username,
            uiState.password
        )

        uiState = if (success) {
            uiState.copy(errorMessage = null)
        } else {
            uiState.copy(errorMessage = "Что-то пошло не так")
        }
    }
}