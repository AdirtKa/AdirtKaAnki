package com.example.adirtkaanki.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager

class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            sessionManager = SessionManager(context.applicationContext)
        ) as T
    }
}