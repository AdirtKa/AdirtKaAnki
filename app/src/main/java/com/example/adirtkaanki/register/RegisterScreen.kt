package com.example.adirtkaanki.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    // onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(context)
    )

    val state = viewModel.uiState

    if (viewModel.registerSuccess) {
        LaunchedEffect(viewModel.registerSuccess) {
            // onRegisterSuccess()
            viewModel.onNavigated()
        }
    }

    RegisterForm(
        username = state.username,
        password = state.password,
        confirmPassword = state.confirmPassword,
        errorMessage = state.errorMessage,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSubmit = viewModel::onSubmit,
        isLoading = state.isLoading,
        onLoginClick = onLoginClick
    )
}