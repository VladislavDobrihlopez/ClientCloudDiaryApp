package com.example.yourdiabetesdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationScreen
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationViewModel
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
                        val viewModel: AuthenticationViewModel = viewModel()
                        val loadingState = viewModel.loadingState
                        val oneTapState = rememberOneTapSignInState()
                        val authResultState = rememberMessageBarState()
                        AuthenticationScreen(
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
                                    onSuccess = { isLoggedInAtlas ->
                                        if (isLoggedInAtlas) {
                                            authResultState.addSuccess("Successfully authorized")
                                            viewModel.setLoading(false)
                                        }
                                    },
                                    onError = { error ->
                                        Log.d("MONGO_ATLAS", error.message.toString())
                                        authResultState.addError(Exception(error))
                                        viewModel.setLoading(false)
                                    })
                            },
                            onReceivingDismissed = { cause ->
                                authResultState.addError(Exception(cause))
                            },
                            loadingState = loadingState.value
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
