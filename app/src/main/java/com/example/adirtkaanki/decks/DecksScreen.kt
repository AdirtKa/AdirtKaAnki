package com.example.adirtkaanki.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }

    if (viewModel.logoutSuccess) {
        LaunchedEffect(viewModel.logoutSuccess) {
            onLogoutSuccess()
            viewModel.onLogoutNavigated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Колоды пользователя: ${uiState.username}",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showCreateDeckDialog = true }
        ) {
            Text("Создать колоду")
        }

        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                if (uiState.decks.isEmpty()) {
                    item {
                        Text(
                            text = "Пока нет колод",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                items(uiState.decks) { deck ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = deck.name,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        LoadingButton(
            modifier = Modifier
                .widthIn(200.dp)
                .fillMaxWidth(),
            text = "Выйти из аккаунта - ${uiState.username}",
            isLoading = uiState.isLoading,
            onClick = viewModel::onLogout
        )
    }

    if (showCreateDeckDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isCreating) {
                    showCreateDeckDialog = false
                    deckName = ""
                }
            },
            title = { Text("Создание колоды") },
            text = {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Название колоды") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createDeck(deckName) {
                            deckName = ""
                            showCreateDeckDialog = false
                        }
                    },
                    enabled = !uiState.isCreating && deckName.trim().isNotEmpty()
                ) {
                    Text(if (uiState.isCreating) "Создание..." else "Создать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!uiState.isCreating) {
                            deckName = ""
                            showCreateDeckDialog = false
                        }
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}