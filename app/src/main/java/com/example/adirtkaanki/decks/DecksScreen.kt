package com.example.adirtkaanki.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.ui.components.LoadingButton


@Composable
fun DecksScreen(
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DecksViewModel = viewModel(factory = DecksViewModelFactory(context))

    val uiState = viewModel.uiState

    if (viewModel.logoutSuccess) {
        LaunchedEffect(viewModel.logoutSuccess) {
            onLogoutSuccess()
            viewModel.onLogoutNavigated()
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        LoadingButton(
            modifier = Modifier
                .safeDrawingPadding()
                .widthIn(200.dp)
                .fillMaxWidth(),
            text = "Выйти",
            isLoading = uiState.isLoading,
            onClick = viewModel::onLogout
        )
    }


}

@Preview(showBackground = true)
@Composable
fun DecksScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        LoadingButton(
            modifier = Modifier
                .safeDrawingPadding()
                .widthIn(max = 120.dp),
            text = "Выйти",
            isLoading = false,
            onClick = {}
        )
    }

}