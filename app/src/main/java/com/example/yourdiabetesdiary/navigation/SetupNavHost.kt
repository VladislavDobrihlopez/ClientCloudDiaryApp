package com.example.yourdiabetesdiary.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationScreen
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationViewModel
import com.example.yourdiabetesdiary.presentation.screens.home.HomeScreen
import com.example.yourdiabetesdiary.util.Constants
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SetupNavHost(
    navHostController: NavHostController,
    startDestination: String,
    keepSplashScreen: (Boolean) -> Unit
) {
    val navigationState = rememberNavigationState(navHostController)

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authenticationRoute(keepSplashScreen = keepSplashScreen) {
            navigationState.navigateToHome()
        }
        homeRoute(keepSplashScreen = keepSplashScreen) {
            navigationState.navigateToWrite()
        }
        diaryRoute()
    }
}

private fun NavGraphBuilder.authenticationRoute(
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
            onTokenReceived = { token ->
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

private fun NavGraphBuilder.homeRoute(
    keepSplashScreen: (Boolean) -> Unit,
    navigateToWriteScreen: () -> Unit
) {
    composable(route = Screen.Home.route) {
        Log.d("TEST_USER", "home_screen")
        keepSplashScreen(false)
        val scope = rememberCoroutineScope()

        HomeScreen(
            onMenuClicked = { /*TODO*/ },
            navigateToWriteScreen = {
                navigateToWriteScreen()
            }
        )
//                scope.launch(Dispatchers.IO) {
//                    App.create(Constants.MONGO_DB_APP_ID).currentUser?.logOut()
//                }
    }
}

private fun NavGraphBuilder.diaryRoute() {
    composable(
        route = Screen.DiaryEntry.route, arguments = listOf(navArgument(
            name = Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY,
        ) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}