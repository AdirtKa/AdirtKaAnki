package com.example.adirtkaanki.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )

    val state = viewModel.uiState

    if (viewModel.loginSuccess) {
        LaunchedEffect(viewModel.loginSuccess) {
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