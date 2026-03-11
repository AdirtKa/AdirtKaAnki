package com.example.adirtkaanki.deckcards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class DeckCardsViewModelFactory(
    private val context: Context,
    private val deckId: String,
    private val deckName: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeckCardsViewModel::class.java)) {
            return DeckCardsViewModel(
                deckId = deckId,
                deckName = deckName,
                sessionManager = SessionManager(context.applicationContext)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
