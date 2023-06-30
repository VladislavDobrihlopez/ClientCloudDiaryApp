package com.example.yourdiabetesdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationScreen
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

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
                        val oneTapState = rememberOneTapSignInState()
                        val authResultState = rememberMessageBarState()
                        AuthenticationScreen(
                            oneTapState = oneTapState,
                            authResultState = authResultState,
                            onButtonClick = {
                                oneTapState.open()
                            },
                            loadingState = oneTapState.opened
                        )
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
