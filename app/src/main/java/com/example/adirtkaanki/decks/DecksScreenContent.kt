package com.example.adirtkaanki.decks

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.ui.components.LoadingButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksScreenContent(
    uiState: DecksUiState,
    showCreateDeckDialog: Boolean,
    showRenameDeckDialog: Boolean,
    deckName: String,
    onDeckNameChange: (String) -> Unit,
    onShowCreateDeckDialog: () -> Unit,
    onDismissCreateDeckDialog: () -> Unit,
    onConfirmCreateDeck: () -> Unit,
    onShowRenameDeckDialog: (Deck) -> Unit,
    onDismissRenameDeckDialog: () -> Unit,
    onConfirmRenameDeck: () -> Unit,
    onShowCardsClick: (Deck) -> Unit,
    onRefresh: () -> Unit,
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
            text = "User decks: ${uiState.username}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowCreateDeckDialog
        ) {
            Text("Create deck")
        }

        uiState.errorMessage?.let { error ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    if (uiState.decks.isEmpty()) {
                        item {
                            Text(
                                text = "No decks yet",
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
                                        onClick = {},
                                        onLongClick = { onDeckLongClick(deck) }
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
                                    text = { Text("Rename deck") },
                                    onClick = { onShowRenameDeckDialog(deck) }
                                )
                                DropdownMenuItem(
                                    text = { Text("View cards") },
                                    onClick = { onShowCardsClick(deck) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = { onDeleteDeckClick(deck) }
                                )
                            }
                        }
                    }
                }
            }
        }

        LoadingButton(
            modifier = Modifier
                .widthIn(200.dp)
                .fillMaxWidth(),
            text = "Log out - ${uiState.username}",
            isLoading = uiState.isLoading,
            onClick = onLogoutClick
        )
    }

    if (showCreateDeckDialog) {
        AlertDialog(
            onDismissRequest = onDismissCreateDeckDialog,
            title = { Text("Create deck") },
            text = {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = onDeckNameChange,
                    label = { Text("Deck name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmCreateDeck,
                    enabled = !uiState.isCreating && deckName.trim().isNotEmpty()
                ) {
                    Text(if (uiState.isCreating) "Creating..." else "Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissCreateDeckDialog,
                    enabled = !uiState.isCreating
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRenameDeckDialog) {
        AlertDialog(
            onDismissRequest = onDismissRenameDeckDialog,
            title = { Text("Rename deck") },
            text = {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = onDeckNameChange,
                    label = { Text("New name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmRenameDeck,
                    enabled = !uiState.isRenaming && deckName.trim().isNotEmpty()
                ) {
                    Text(if (uiState.isRenaming) "Saving..." else "Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRenameDeckDialog,
                    enabled = !uiState.isRenaming
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
