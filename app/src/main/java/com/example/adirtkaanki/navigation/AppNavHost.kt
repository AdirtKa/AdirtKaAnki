package com.example.adirtkaanki.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adirtkaanki.login.LoginScreen
import com.example.adirtkaanki.register.RegisterScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        // modifier = Modifier.safeDrawingPadding(),
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(
            Routes.LOGIN,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },

                onLoginSuccess = {
                    navController.navigate(Routes.DECKS)
                }

            )
        }

        composable(
            Routes.REGISTER,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(
            Routes.DECKS,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            Text("Main")
        }
    }
}