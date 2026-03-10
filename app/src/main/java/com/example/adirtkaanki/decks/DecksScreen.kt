package com.example.adirtkaanki.decks

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.ui.components.LoadingButton

@Composable
fun DecksScreen(
//    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: DecksViewModel = viewModel(factory = DecksViewModelFactory(context))
    val uiState = viewModel.uiState

    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    var selectedDeckForMenu by remember { mutableStateOf<Deck?>(null) }

    if (viewModel.logoutSuccess) {
        LaunchedEffect(viewModel.logoutSuccess) {
//            onLogoutSuccess()
            viewModel.onLogoutNavigated()
        }
    }

    DecksScreenContent(
        uiState = uiState,
        showCreateDeckDialog = showCreateDeckDialog,
        deckName = deckName,
        onDeckNameChange = { deckName = it },
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
        onLogoutClick = viewModel::onLogout,
        selectedDeckForMenu = selectedDeckForMenu,
        onDeckLongClick = { deck -> selectedDeckForMenu = deck },
        onDismissDeckMenu = { selectedDeckForMenu = null },
        onDeleteDeckClick = { deck ->
            selectedDeckForMenu = null
            println(deck)
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
        deckName = "",
        onDeckNameChange = {},
        onShowCreateDeckDialog = {},
        onDismissCreateDeckDialog = {},
        onConfirmCreateDeck = {},
        onLogoutClick = {},
        onDeckLongClick = {},
        onDismissDeckMenu = {},
        onDeleteDeckClick = {},
        selectedDeckForMenu = null,
    )

}