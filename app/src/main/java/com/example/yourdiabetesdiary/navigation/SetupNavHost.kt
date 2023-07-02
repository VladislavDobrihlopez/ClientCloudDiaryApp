package com.example.yourdiabetesdiary.navigation

import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.yourdiabetesdiary.presentation.components.CustomAlertDialog
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationScreen
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationViewModel
import com.example.yourdiabetesdiary.presentation.screens.home.HomeScreen
import com.example.yourdiabetesdiary.util.Constants
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        homeRoute(
            keepSplashScreen = keepSplashScreen,
            navigateToWriteScreen = {
                navigationState.navigateToWrite()
            },
            navigateBackToAuthScreen = {
                navigationState.navigateToAuth()
            }
        )
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
    navigateToWriteScreen: () -> Unit,
    navigateBackToAuthScreen: () -> Unit
) {
    composable(route = Screen.Home.route) {
        Log.d("TEST_USER", "home_screen")
        keepSplashScreen(false)
        val scope = rememberCoroutineScope()
        val navDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val openDialogState = remember {
            mutableStateOf(false)
        }

        HomeScreen(
            drawerState = navDrawerState,
            onMenuClicked = {
                scope.launch {
                    navDrawerState.open()
                }
            },
            navigateToWriteScreen = {
                navigateToWriteScreen()
            },
            onSignOut = {
                openDialogState.value = true
            }
        )

        CustomAlertDialog(
            title = "Sign out dialog",
            message = "Are you sure you want to sign out from the account?",
            isDialogOpened = openDialogState,
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(Constants.MONGO_DB_APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateBackToAuthScreen()
                        }
                    }
                }
            },
            onDialogClosed = {
                openDialogState.value = false
            }
        )
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