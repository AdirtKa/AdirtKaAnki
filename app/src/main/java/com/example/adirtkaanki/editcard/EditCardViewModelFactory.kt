package com.example.adirtkaanki.editcard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class EditCardViewModelFactory(
    private val context: Context,
    private val cardId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sessionManager = SessionManager(context)
        return EditCardViewModel(cardId = cardId, sessionManager = sessionManager) as T
    }
}
