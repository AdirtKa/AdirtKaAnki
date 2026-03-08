package com.example.adirtkaanki.navigation


import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adirtkaanki.data.session.SessionState
import com.example.adirtkaanki.data.session.SessionViewModel
import com.example.adirtkaanki.login.LoginScreen
import com.example.adirtkaanki.register.RegisterScreen
import com.example.adirtkaanki.splash.SplashScreen

@Composable
fun AppNavHost(
    sessionViewModel: SessionViewModel
) {
    val navController = rememberNavController()
    val sessionState = sessionViewModel.sessionState

    NavHost(
        // modifier = Modifier.safeDrawingPadding(),
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(sessionState = sessionState)

            LaunchedEffect(sessionState) {
                when (sessionState) {
                    SessionState.Loading -> Unit
                    SessionState.LoggedIn -> {
                        navController.navigate(Routes.DECKS) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }

                    SessionState.LoggedOut -> {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            }
        }

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
                    navController.navigate(Routes.DECKS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
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
                },

                onRegisterSuccess = {
                    navController.navigate(Routes.DECKS) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
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