package com.unreal.medisageai.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import androidx.navigation.popUpTo
import com.unreal.medisageai.ui.auth.CreateAccountScreen
import com.unreal.medisageai.ui.auth.LoginScreen
import com.unreal.medisageai.ui.chat.ChatScreen

private const val TRANSITION_MS = 350

/**
 * The app's single navigation graph. Starts at [Login] and pushes/pops with a buttery
 * horizontal slide-and-fade (the outgoing screen parallax-shifts a quarter width).
 *
 * On successful login or registration the auth back stack is popped inclusive of [Login],
 * so the system back button from the chat exits the app rather than returning to a form.
 */
@Composable
fun MediSageNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Login,
        enterTransition = {
            slideInHorizontally(tween(TRANSITION_MS, easing = FastOutSlowInEasing)) { it } +
                fadeIn(tween(TRANSITION_MS))
        },
        exitTransition = {
            slideOutHorizontally(tween(TRANSITION_MS, easing = FastOutSlowInEasing)) { -it / 4 } +
                fadeOut(tween(TRANSITION_MS))
        },
        popEnterTransition = {
            slideInHorizontally(tween(TRANSITION_MS, easing = FastOutSlowInEasing)) { -it / 4 } +
                fadeIn(tween(TRANSITION_MS))
        },
        popExitTransition = {
            slideOutHorizontally(tween(TRANSITION_MS, easing = FastOutSlowInEasing)) { it } +
                fadeOut(tween(TRANSITION_MS))
        },
    ) {
        composable<Login> {
            LoginScreen(
                onLoginClick = { navController.enterChatClearingAuth() },
                onCreateAccountClick = { navController.navigate(CreateAccount) },
            )
        }
        composable<CreateAccount> {
            CreateAccountScreen(
                onSignUpClick = { navController.enterChatClearingAuth() },
                onBackToLogin = { navController.popBackStack() },
            )
        }
        composable<ActiveChat> {
            ChatScreen()
        }
    }
}

/** Enters the chat and clears the entire auth flow (Login + Create Account) from the back stack. */
private fun NavController.enterChatClearingAuth() {
    navigate(ActiveChat()) {
        popUpTo<Login> { inclusive = true }
        launchSingleTop = true
    }
}
