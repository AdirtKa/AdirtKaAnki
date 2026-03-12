package com.example.adirtkaanki.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.adirtkaanki.carddetail.CardDetailScreen
import com.example.adirtkaanki.createcard.CreateCardScreen
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.session.SessionState
import com.example.adirtkaanki.data.session.SessionViewModel
import com.example.adirtkaanki.deckcards.DeckCardsScreen
import com.example.adirtkaanki.decks.DecksScreen
import com.example.adirtkaanki.editcard.EditCardScreen
import com.example.adirtkaanki.login.LoginScreen
import com.example.adirtkaanki.register.RegisterScreen
import com.example.adirtkaanki.splash.SplashScreen

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
                    navController.navigate(Routes.DECKS) {
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

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }, popEnterTransition = { EnterTransition.None }, popExitTransition = { ExitTransition.None }) {
            SplashScreen(sessionState = sessionState)
        }

        composable(Routes.LOGIN, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }, popEnterTransition = { EnterTransition.None }, popExitTransition = { ExitTransition.None }) {
            LoginScreen(onRegisterClick = { navController.navigate(Routes.REGISTER) })
        }

        composable(Routes.REGISTER, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }, popEnterTransition = { EnterTransition.None }, popExitTransition = { ExitTransition.None }) {
            RegisterScreen(onLoginClick = { navController.navigate(Routes.LOGIN) })
        }

        composable(Routes.DECKS, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }, popEnterTransition = { EnterTransition.None }, popExitTransition = { ExitTransition.None }) {
            DecksScreen(onShowCardsClick = { deck: Deck -> navController.navigate(Routes.deckCards(deck.id, deck.name)) })
        }

        composable(
            route = Routes.DECK_CARDS,
            arguments = listOf(
                navArgument(Routes.DECK_CARDS_ID_ARG) { type = NavType.StringType },
                navArgument(Routes.DECK_CARDS_NAME_ARG) { type = NavType.StringType }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString(Routes.DECK_CARDS_ID_ARG).orEmpty()
            val deckName = Uri.decode(backStackEntry.arguments?.getString(Routes.DECK_CARDS_NAME_ARG).orEmpty())
            val refreshSignal = backStackEntry.savedStateHandle.getStateFlow("cards_changed", false)

            DeckCardsScreen(
                deckId = deckId,
                deckName = deckName,
                refreshSignal = refreshSignal,
                onRefreshHandled = { backStackEntry.savedStateHandle["cards_changed"] = false },
                onBackClick = { navController.popBackStack() },
                onCreateCardClick = { navController.navigate(Routes.createCard(deckId, deckName)) },
                onCardClick = { cardId -> navController.navigate(Routes.cardDetail(cardId)) }
            )
        }

        composable(
            route = Routes.CREATE_CARD,
            arguments = listOf(
                navArgument(Routes.CREATE_CARD_ID_ARG) { type = NavType.StringType },
                navArgument(Routes.CREATE_CARD_NAME_ARG) { type = NavType.StringType }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString(Routes.CREATE_CARD_ID_ARG).orEmpty()
            CreateCardScreen(
                deckId = deckId,
                onBackClick = { navController.popBackStack() },
                onCardCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("cards_changed", true)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.CARD_DETAIL,
            arguments = listOf(navArgument(Routes.CARD_DETAIL_ID_ARG) { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString(Routes.CARD_DETAIL_ID_ARG).orEmpty()
            val refreshSignal = backStackEntry.savedStateHandle.getStateFlow("card_updated", false)

            CardDetailScreen(
                cardId = cardId,
                refreshSignal = refreshSignal,
                onRefreshHandled = { backStackEntry.savedStateHandle["card_updated"] = false },
                onBackClick = {
                    if (backStackEntry.savedStateHandle.get<Boolean>("card_updated") == true) {
                        navController.previousBackStackEntry?.savedStateHandle?.set("cards_changed", true)
                    }
                    navController.popBackStack()
                },
                onEditClick = { navController.navigate(Routes.editCard(cardId)) }
            )
        }

        composable(
            route = Routes.EDIT_CARD,
            arguments = listOf(navArgument(Routes.EDIT_CARD_ID_ARG) { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString(Routes.EDIT_CARD_ID_ARG).orEmpty()
            EditCardScreen(
                cardId = cardId,
                onBackClick = { navController.popBackStack() },
                onCardUpdated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("card_updated", true)
                    navController.popBackStack()
                }
            )
        }
    }
}
