package com.example.adirtkaanki.decks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class DecksViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DecksViewModel(
            sessionManager = SessionManager(context.applicationContext)
        ) as T
    }

}