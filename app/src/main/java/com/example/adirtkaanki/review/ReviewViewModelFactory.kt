package com.example.adirtkaanki.review

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class ReviewViewModelFactory(
    private val context: Context,
    private val deckId: String,
    private val deckName: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(
                deckId = deckId,
                deckName = deckName,
                sessionManager = SessionManager(context.applicationContext)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
