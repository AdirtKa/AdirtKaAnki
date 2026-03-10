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
import com.example.adirtkaanki.decks.DecksScreen

@Composable
fun AppNavHost(
    sessionViewModel: SessionViewModel
) {
    val navController = rememberNavController()
    val sessionState = sessionViewModel.sessionState

    LaunchedEffect(sessionState) {
        when (sessionState) {
            SessionState.Loading -> Unit

            SessionState.LoggedIn -> {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute != Routes.DECKS) {
                    navController.navigate(
                        Routes.DECKS,
                    ) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            SessionState.LoggedOut -> {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute != Routes.LOGIN) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(
            Routes.SPLASH,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            SplashScreen(sessionState = sessionState)
        }

        composable(
            Routes.LOGIN, enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
            )
        }

        composable(
            Routes.REGISTER,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
            )
        }

        composable(
            Routes.DECKS, enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }) {
            DecksScreen()
        }
    }
}