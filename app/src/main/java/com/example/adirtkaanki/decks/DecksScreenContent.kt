package com.example.adirtkaanki.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.ui.components.LoadingButton

@Composable
fun DecksScreenContent(
    uiState: DecksUiState,
    showCreateDeckDialog: Boolean,
    deckName: String,
    onDeckNameChange: (String) -> Unit,
    onShowCreateDeckDialog: () -> Unit,
    onDismissCreateDeckDialog: () -> Unit,
    onConfirmCreateDeck: () -> Unit,
    onLogoutClick: () -> Unit,
    selectedDeckForMenu: Deck?,
    onDeckLongClick: (Deck) -> Unit,
    onDismissDeckMenu: () -> Unit,
    onDeleteDeckClick: (Deck) -> Unit
) {
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
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowCreateDeckDialog
        ) {
            Text("Создать колоду")
        }

        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()

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
                modifier = Modifier
                    .weight(1f),
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

                items(
                    items = uiState.decks,
                    key = { deck -> deck.id }
                ) { deck ->
                    Box {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {println("click $deck")},
                                    onLongClick = {
                                        onDeckLongClick(deck)
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = deck.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
                            expanded = selectedDeckForMenu?.id == deck.id,
                            shape = RoundedCornerShape(20.dp),
                            offset = DpOffset(x = 0.dp, y = 8.dp),
                            onDismissRequest = onDismissDeckMenu
                        ) {
                            DropdownMenuItem(

                                text = {Text("Удалить")},
                                onClick = {onDeleteDeckClick(deck)}
                            )
                        }
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
            onClick = onLogoutClick
        )
    }

    if (showCreateDeckDialog) {
        AlertDialog(
            onDismissRequest = onDismissCreateDeckDialog,
            title = { Text("Создание колоды") },
            text = {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = onDeckNameChange,
                    label = { Text("Название колоды") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmCreateDeck,
                    enabled = !uiState.isCreating && deckName.trim().isNotEmpty()
                ) {
                    Text(if (uiState.isCreating) "Создание..." else "Создать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissCreateDeckDialog,
                    enabled = !uiState.isCreating
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}