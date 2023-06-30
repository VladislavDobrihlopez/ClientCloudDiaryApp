package com.example.yourdiabetesdiary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun SetupNavHost(
    navHostController: NavHostController,
    startDestination: String,
    authenticationScreenContent: @Composable () -> Unit,
    homeScreenContent: @Composable () -> Unit,
    diaryScreenContent: @Composable () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authenticationRoute(authenticationScreenContent)
        homeRoute(homeScreenContent)
        diaryRoute(diaryScreenContent)
    }
}

private fun NavGraphBuilder.authenticationRoute(authenticationScreenContent: @Composable () -> Unit) {
    composable(route = Screen.Authentication.route) {
        authenticationScreenContent()
    }
}

private fun NavGraphBuilder.homeRoute(homeScreenContent: @Composable () -> Unit) {
    composable(route = Screen.Home.route) {
        homeScreenContent()
    }
}

private fun NavGraphBuilder.diaryRoute(diaryScreenContent: @Composable () -> Unit) {
    composable(
        route = Screen.DiaryEntry.route, arguments = listOf(navArgument(
            name = Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY,
        ) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        diaryScreenContent()
    }
}