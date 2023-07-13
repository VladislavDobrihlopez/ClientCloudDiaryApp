package com.example.yourdiabetesdiary

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.navigation.SetupNavHost
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme
import com.example.yourdiabetesdiary.util.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)

        var keepDisplayingSplash = true

        installSplashScreen().setKeepOnScreenCondition {
            keepDisplayingSplash
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            YourDiabetesDiaryTheme(dynamicColor = false) {
                val navController = rememberNavController()

                SetupNavHost(
                    navHostController = navController,
                    startDestination = getStartScreenDestination(),
                    keepSplashScreen = { shouldKeep ->
                        keepDisplayingSplash = shouldKeep
                    },
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
