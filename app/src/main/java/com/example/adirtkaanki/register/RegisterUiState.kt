package com.example.adirtkaanki.register

data class RegisterUiState (
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
    )