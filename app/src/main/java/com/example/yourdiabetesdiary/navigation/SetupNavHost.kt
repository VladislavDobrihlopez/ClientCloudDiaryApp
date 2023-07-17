package com.example.yourdiabetesdiary.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.auth.navigation.authenticationRoute
import com.example.composition.navigation.diaryRoute
import com.example.home.navigation.homeRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun SetupNavHost(
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
            navigateToComposeScreenWithArguments = { chosenDiary ->
                Log.d("TEST_STORING", "navigated id: $chosenDiary")
                navigationState.navigateToCompose(diaryId = chosenDiary)
            },
            navigateToComposeScreen = {
                navigationState.navigateToCompose()
            },
            navigateBackToAuthScreen = {
                navigationState.navigateToAuth()
            }
        )
        diaryRoute(navigateBack = {
            navigationState.navigateBack()
        })
    }
}
