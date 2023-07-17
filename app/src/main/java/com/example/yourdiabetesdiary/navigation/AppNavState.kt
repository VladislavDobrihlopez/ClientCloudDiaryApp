package com.example.yourdiabetesdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.util.Screen

internal class AppNavState(private val navController: NavController) {
    fun navigateToHome() {
        navController.navigate(Screen.Home.route)
    }

    fun navigateToCompose(diaryId: String = "") {
        if (diaryId.isEmpty()) {
            navController.navigate(Screen.DiaryEntry.route) {
                launchSingleTop = true
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
            }
        } else {
            navController.navigate(Screen.DiaryEntry.passArgs(diaryId)) {
                launchSingleTop = true
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
            }
        }
    }

    fun navigateToAuth() {
        navController.popBackStack()
        navController.navigate(Screen.Authentication.route)
    }

    fun navigateBack() {
        navController.popBackStack()
        navController.navigate(Screen.Home.route) {
            restoreState = true
        }
    }
}

@Composable
internal fun rememberNavigationState(navController: NavController = rememberNavController()): AppNavState {
    return remember {
        AppNavState(navController)
    }
}