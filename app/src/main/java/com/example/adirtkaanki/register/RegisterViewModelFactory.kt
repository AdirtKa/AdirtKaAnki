package com.example.adirtkaanki.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class RegisterViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterViewModel(
            sessionManager = SessionManager(context.applicationContext)
        ) as T
    }
}