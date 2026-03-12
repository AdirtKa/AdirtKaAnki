package com.example.adirtkaanki.decks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckCardStats
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
            onClick = onShowCreateDeckDialog,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(
                text = "Create deck",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error
            )
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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    if (uiState.decks.isEmpty()) {
                        item {
                            Text(
                                text = "No decks yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    items(
                        items = uiState.decks,
                        key = { deck -> deck.id }
                    ) { deck ->
                        Box {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { onDeckLongClick(deck) }
                                    )
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = deck.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                DeckStatsRow(
                                    stats = uiState.deckStats[deck.id] ?: DeckCardStats(0, 0, 0)
                                )
                            }

                            DropdownMenu(
                                modifier = Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                                expanded = selectedDeckForMenu?.id == deck.id,
                                shape = RoundedCornerShape(20.dp),
                                containerColor = MaterialTheme.colorScheme.background,
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                                offset = DpOffset(x = 0.dp, y = 8.dp),
                                onDismissRequest = onDismissDeckMenu
                            ) {
                                DropdownMenuItem(
                                    colors = dropdownMenuItemColors(),
                                    text = { Text("Rename deck") },
                                    onClick = { onShowRenameDeckDialog(deck) }
                                )
                                DropdownMenuItem(
                                    colors = dropdownMenuItemColors(),
                                    text = { Text("View cards") },
                                    onClick = { onShowCardsClick(deck) }
                                )
                                DropdownMenuItem(
                                    colors = dropdownMenuItemColors(),
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
            modifier = Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            ),
            onDismissRequest = onDismissCreateDeckDialog,
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = "Create deck",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
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
                DialogActionButton(
                    text = if (uiState.isCreating) "Creating..." else "Create",
                    enabled = !uiState.isCreating && deckName.trim().isNotEmpty(),
                    onClick = onConfirmCreateDeck
                )
            },
            dismissButton = {
                DialogActionButton(
                    text = "Cancel",
                    enabled = !uiState.isCreating,
                    onClick = onDismissCreateDeckDialog
                )
            }
        )
    }

    if (showRenameDeckDialog) {
        AlertDialog(
            modifier = Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(24.dp)
            ),
            onDismissRequest = onDismissRenameDeckDialog,
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = "Rename deck",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
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
                DialogActionButton(
                    text = if (uiState.isRenaming) "Saving..." else "Save",
                    enabled = !uiState.isRenaming && deckName.trim().isNotEmpty(),
                    onClick = onConfirmRenameDeck
                )
            },
            dismissButton = {
                DialogActionButton(
                    text = "Cancel",
                    enabled = !uiState.isRenaming,
                    onClick = onDismissRenameDeckDialog
                )
            }
        )
    }
}

@Composable
private fun DeckStatsRow(stats: DeckCardStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeckStatChip(
            modifier = Modifier.weight(1f),
            label = "Not studied",
            value = stats.notStudied.toString(),
            accentColor = MaterialTheme.colorScheme.tertiary
        )
        DeckStatChip(
            modifier = Modifier.weight(1f),
            label = "Correct",
            value = stats.answeredCorrectly.toString(),
            accentColor = MaterialTheme.colorScheme.primary
        )
        DeckStatChip(
            modifier = Modifier.weight(1f),
            label = "Incorrect",
            value = stats.answeredIncorrectly.toString(),
            accentColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DeckStatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accentColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun dropdownMenuItemColors() = MenuDefaults.itemColors(
    textColor = MaterialTheme.colorScheme.onBackground,
    leadingIconColor = MaterialTheme.colorScheme.onBackground,
    trailingIconColor = MaterialTheme.colorScheme.onBackground,
    disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
    disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
)


@Composable
private fun DialogActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
