package com.example.auth.navigation

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.auth.AuthenticationScreen
import com.example.auth.AuthenticationViewModel
import com.example.util.Screen
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

fun NavGraphBuilder.authenticationRoute(
    keepSplashScreen: (Boolean) -> Unit,
    navigateToHome: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val isUserSignedIn = viewModel.authenticationState
        val loadingState = viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val authResultState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = isUserSignedIn.value,
            onScreenIsReady = {
                keepSplashScreen(false)
            },
            oneTapState = oneTapState,
            authResultState = authResultState,
            onButtonClick = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulFirebaseSignIn = { token ->
                Log.d("MONGO_ATLAS", token)
                viewModel.signInWithMongoAtlas(
                    token = token,
                    onSuccess = {
                        authResultState.addSuccess("Successfully authorized")
                    },
                    onError = { error ->
                        Log.d("MONGO_ATLAS", error.message.toString())
                        authResultState.addError(Exception(error))
                    })
            },
            onFailedSignIn = { ex ->
                authResultState.addError(Exception("Auth error: ${ex.message}"))
                viewModel.setLoading(false)
            },
            onReceivingDismissed = { cause ->
                authResultState.addError(Exception(cause))
                viewModel.setLoading(false)
            },
            loadingState = loadingState.value,
            navigateToHome = {
                navigateToHome()
            }
        )
    }
}