package com.example.adirtkaanki.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import com.example.adirtkaanki.data.repository.AuthRepository
import com.example.adirtkaanki.data.repository.LoginResult
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val api = ApiFactory.createAuthApiService(sessionManager)
    private val repository = AuthRepository(Database(), api, sessionManager)

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
            val result = repository.login(uiState.username, uiState.password)

            if (result.isSuccess){
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = null
                )

                loginSuccess = true
            } else {
                val errorText = when (val e = result.exceptionOrNull()) {
                    is HttpException -> "Ошибка сервера: ${e.code()}"
                    else -> e?.message ?: "Не удалось выполнить вход"
                }

                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = errorText
                )
                loginSuccess = false
            }
        }
    }

    fun onNavigated() {
        loginSuccess = false
    }
}