package com.example.yourdiabetesdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            YourDiabetesDiaryTheme {
                val navController = rememberNavController()
                SetupNavHost(
                    navHostController = navController,
                    startDestination = Screen.Authentication.route,
                    authenticationScreenContent = {

                    },
                    homeScreenContent = {

                    },
                    diaryScreenContent = {

                    }
                )
            }
        }
    }
}
