package com.example.adirtkaanki.register

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onLoginClick: () -> Unit
) {
    val state = viewModel.uiState

    RegisterForm(
        username = state.username,
        password = state.password,
        confirmPassword = state.confirmPassword,
        errorMessage = state.errorMessage,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSubmit = viewModel::onSubmit,
        onLoginClick = onLoginClick
    )
}