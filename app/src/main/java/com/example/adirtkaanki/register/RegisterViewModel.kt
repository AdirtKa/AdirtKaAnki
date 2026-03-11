package com.example.adirtkaanki.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val api = ApiFactory.createAuthApiService(sessionManager)
    private val repository = AuthRepository(api, sessionManager)
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
        uiState = uiState.copy(
            isLoading = true,
            username = uiState.username.trim()
        )

        if (
            uiState.username.isBlank() ||
            uiState.password.isBlank() ||
            uiState.confirmPassword.isBlank()
        ) {
            uiState = uiState.copy(
                errorMessage = "Заполните все поля",
                isLoading = false
            )
            return
        }

        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(
                errorMessage = "Пароли не совпадают",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            val result = repository.register(uiState.username, uiState.password)

            if (result.isSuccess) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = null
                )

                registerSuccess = true
            } else {
                val errorText = when (val e = result.exceptionOrNull()) {
                    is HttpException -> "Ошибка сервера: ${e.code()}"
                    else -> e?.message ?: "Не удалось выполнить вход"
                }
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = errorText
                )
                registerSuccess = false
            }

        }

    }

    fun onNavigated() {
        registerSuccess = false
    }
}
