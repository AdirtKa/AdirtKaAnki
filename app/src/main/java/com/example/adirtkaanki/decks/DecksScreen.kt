package com.example.adirtkaanki.decks

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.data.model.Deck

@Composable
fun DecksScreen(
    onDeckClick: (Deck) -> Unit,
    onShowCardsClick: (Deck) -> Unit,
    onShowStatsClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DecksViewModel = viewModel(factory = DecksViewModelFactory(context))
    val uiState = viewModel.uiState

    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var showRenameDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    var selectedDeckForMenu by remember { mutableStateOf<Deck?>(null) }
    var deckForRename by remember { mutableStateOf<Deck?>(null) }

    if (viewModel.logoutSuccess) {
        LaunchedEffect(viewModel.logoutSuccess) {
            viewModel.onLogoutNavigated()
        }
    }

    DecksScreenContent(
        uiState = uiState,
        showCreateDeckDialog = showCreateDeckDialog,
        showRenameDeckDialog = showRenameDeckDialog,
        deckName = deckName,
        onDeckNameChange = { deckName = it.take(100) },
        onShowCreateDeckDialog = { showCreateDeckDialog = true },
        onDismissCreateDeckDialog = {
            if (!uiState.isCreating) {
                showCreateDeckDialog = false
                deckName = ""
            }
        },
        onConfirmCreateDeck = {
            viewModel.createDeck(deckName) {
                deckName = ""
                showCreateDeckDialog = false
            }
        },
        onShowRenameDeckDialog = { deck ->
            deckForRename = deck
            deckName = deck.name
            showRenameDeckDialog = true
            selectedDeckForMenu = null
        },
        onDeckClick = { deck ->
            selectedDeckForMenu = null
            onDeckClick(deck)
        },
        onDismissRenameDeckDialog = {
            if (!uiState.isRenaming) {
                showRenameDeckDialog = false
                deckForRename = null
                deckName = ""
            }
        },
        onConfirmRenameDeck = {
            val deck = deckForRename
            if (deck != null) {
                viewModel.renameDeck(deck, deckName) {
                    deckName = ""
                    deckForRename = null
                    showRenameDeckDialog = false
                }
            }
        },
        onShowCardsClick = { deck ->
            selectedDeckForMenu = null
            onShowCardsClick(deck)
        },
        onShowStatsClick = onShowStatsClick,
        onRefresh = viewModel::refresh,
        onLogoutClick = viewModel::onLogout,
        selectedDeckForMenu = selectedDeckForMenu,
        onDeckLongClick = { deck -> selectedDeckForMenu = deck },
        onDismissDeckMenu = { selectedDeckForMenu = null },
        onDeleteDeckClick = { deck ->
            selectedDeckForMenu = null
            viewModel.deleteDeck(deck)
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DecksScreenPreview() {
    DecksScreenContent(
        uiState = DecksUiState(
            isLoading = false,
            isCreating = false,
            username = "daniil",
            decks = listOf(
                Deck(
                    id = "550e8400-e29b-41d4-a716-446655440000",
                    name = "English words"
                ),
                Deck(
                    id = "550e8400-e29b-41d4-a716-446655440001",
                    name = "Japanese N5"
                )
            ),
            errorMessage = null
        ),
        showCreateDeckDialog = false,
        showRenameDeckDialog = false,
        deckName = "",
        onDeckNameChange = {},
        onShowCreateDeckDialog = {},
        onDismissCreateDeckDialog = {},
        onConfirmCreateDeck = {},
        onShowRenameDeckDialog = {},
        onDeckClick = {},
        onDismissRenameDeckDialog = {},
        onConfirmRenameDeck = {},
        onShowCardsClick = {},
        onShowStatsClick = {},
        onRefresh = {},
        onLogoutClick = {},
        onDeckLongClick = {},
        onDismissDeckMenu = {},
        onDeleteDeckClick = {},
        selectedDeckForMenu = null,
    )
}
