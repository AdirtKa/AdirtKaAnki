package com.example.adirtkaanki.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state = viewModel.uiState

    if (state.isLoggedIn) {
        LaunchedEffect(state.isLoggedIn) {
            onLoginSuccess()
            viewModel.onNavigated()
        }
    }

    LoginForm(
        username = state.username,
        password = state.password,
        errorMessage = state.errorMessage,
        isLoading = state.isLoading,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::onSubmit,
        onRegisterClick = onRegisterClick
    )
}