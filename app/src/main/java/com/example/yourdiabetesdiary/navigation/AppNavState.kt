package com.example.yourdiabetesdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

class AppNavState(private val navController: NavController) {
    fun navigateToHome() {
        navController.popBackStack()
        navController.navigate(Screen.Home.route)
    }
}

@Composable
fun rememberNavigationState(navController: NavController = rememberNavController()): AppNavState {
    return remember {
        AppNavState(navController)
    }
}