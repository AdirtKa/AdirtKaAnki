package com.example.adirtkaanki.navigation

import android.net.Uri

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DECKS = "decks"
    const val SPLASH = "splash"

    private const val DECK_ID_ARG = "deckId"
    private const val DECK_NAME_ARG = "deckName"

    const val DECK_CARDS = "deck_cards/{$DECK_ID_ARG}/{$DECK_NAME_ARG}"

    fun deckCards(deckId: String, deckName: String): String {
        return "deck_cards/$deckId/${Uri.encode(deckName)}"
    }

    const val DECK_CARDS_ID_ARG = DECK_ID_ARG
    const val DECK_CARDS_NAME_ARG = DECK_NAME_ARG
}
