package com.example.adirtkaanki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.adirtkaanki.data.session.SessionManager
import com.example.adirtkaanki.data.session.SessionViewModel
import com.example.adirtkaanki.data.session.SessionViewModelFactory
import com.example.adirtkaanki.navigation.AppNavHost
import com.example.adirtkaanki.ui.theme.AdirtKaAnkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionViewModel = ViewModelProvider(
            this,
            SessionViewModelFactory(
                SessionManager(applicationContext)
            )
        )[SessionViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            AdirtKaAnkiTheme {
                AppNavHost(sessionViewModel = sessionViewModel)
            }
        }
    }
}