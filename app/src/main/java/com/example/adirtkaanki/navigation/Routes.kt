package com.example.adirtkaanki.navigation

import android.net.Uri

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DECKS = "decks"
    const val SPLASH = "splash"

    private const val DECK_ID_ARG = "deckId"
    private const val DECK_NAME_ARG = "deckName"
    private const val CARD_ID_ARG = "cardId"

    const val DECK_CARDS = "deck_cards/{$DECK_ID_ARG}/{$DECK_NAME_ARG}"
    const val CREATE_CARD = "create_card/{$DECK_ID_ARG}/{$DECK_NAME_ARG}"
    const val CARD_DETAIL = "card_detail/{$CARD_ID_ARG}"
    const val EDIT_CARD = "edit_card/{$CARD_ID_ARG}"

    fun deckCards(deckId: String, deckName: String): String {
        return "deck_cards/$deckId/${Uri.encode(deckName)}"
    }

    fun createCard(deckId: String, deckName: String): String {
        return "create_card/$deckId/${Uri.encode(deckName)}"
    }

    fun cardDetail(cardId: String): String {
        return "card_detail/$cardId"
    }

    fun editCard(cardId: String): String {
        return "edit_card/$cardId"
    }

    const val DECK_CARDS_ID_ARG = DECK_ID_ARG
    const val DECK_CARDS_NAME_ARG = DECK_NAME_ARG
    const val CREATE_CARD_ID_ARG = DECK_ID_ARG
    const val CREATE_CARD_NAME_ARG = DECK_NAME_ARG
    const val CARD_DETAIL_ID_ARG = CARD_ID_ARG
    const val EDIT_CARD_ID_ARG = CARD_ID_ARG
}
