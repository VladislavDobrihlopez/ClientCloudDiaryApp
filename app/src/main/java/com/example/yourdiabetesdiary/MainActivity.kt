package com.example.yourdiabetesdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.yourdiabetesdiary.navigation.rememberNavigationState
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationScreen
import com.example.yourdiabetesdiary.presentation.screens.auth.AuthenticationViewModel
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme
import com.example.yourdiabetesdiary.util.Constants
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepDisplayingSplash = true

        installSplashScreen().setKeepOnScreenCondition {
            keepDisplayingSplash
        }

        setContent {
            YourDiabetesDiaryTheme {
                val navController = rememberNavController()
                val navigationState = rememberNavigationState(navController)

                SetupNavHost(
                    navHostController = navController,
                    startDestination = getStartScreenDestination(),
                    authenticationScreenContent = {
                        val viewModel: AuthenticationViewModel = viewModel()
                        val isUserSignedIn = viewModel.authenticationState
                        val loadingState = viewModel.loadingState
                        val oneTapState = rememberOneTapSignInState()
                        val authResultState = rememberMessageBarState()

                        AuthenticationScreen(
                            authenticated = isUserSignedIn.value,
                            onScreenIsReady = {
                                keepDisplayingSplash = false
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
                                    onSuccess = { isLoggedInAtlas ->
                                        if (isLoggedInAtlas) {
                                            authResultState.addSuccess("Successfully authorized")
                                        }
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
                                navigationState.navigateToHome()
                            }
                        )
                    },
                    homeScreenContent = {
                        Log.d("TEST_USER", "home_screen")
                        keepDisplayingSplash = false
                        val scope = rememberCoroutineScope()
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    App.create(Constants.MONGO_DB_APP_ID).currentUser?.logOut()
                                }

                            }) {
                                Text(text = "Logout")
                            }
                        }
                    },
                    diaryScreenContent = {

                    }
                )
            }
        }
    }

    private fun getStartScreenDestination(): String {
        val user = App.Companion.create(Constants.MONGO_DB_APP_ID).currentUser
        Log.d("TEST_USER", user.toString())
        return if (user == null || !user.loggedIn) Screen.Authentication.route else Screen.Home.route
    }
}
