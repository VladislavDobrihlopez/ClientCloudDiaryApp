package com.example.home.navigation

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.ui.components.CustomAlertDialog
import com.example.util.Constants
import com.example.util.RequestState
import com.example.util.Screen
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeRoute(
    keepSplashScreen: (Boolean) -> Unit,
    navigateToComposeScreenWithArguments: (String) -> Unit,
    navigateToComposeScreen: () -> Unit,
    navigateBackToAuthScreen: () -> Unit
) {
    composable(route = Screen.Home.route) {
        Log.d("TEST_USER", "home_screen")
        val scope = rememberCoroutineScope()
        val navDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val openDialogState = remember {
            mutableStateOf(false)
        }
        val openDeletionDialogState = remember {
            mutableStateOf(false)
        }

        val viewModel: com.example.home.HomeViewModel = hiltViewModel()
        val state = viewModel.diaries

        LaunchedEffect(key1 = state.value) {
            if (!(state.value == RequestState.Loading || state.value == RequestState.Idle)) {
                keepSplashScreen(false)
            }
        }

        com.example.home.HomeScreen(
            drawerState = navDrawerState,
            state = state.value,
            onMenuClicked = {
                scope.launch {
                    navDrawerState.open()
                }
            },
            navigateToCompositionScreen = {
                navigateToComposeScreen()
            },
            onSignOut = {
                openDialogState.value = true
            },
            onDiaryChose = navigateToComposeScreenWithArguments,
            onDeleteAllDiariesClicked = {
                openDeletionDialogState.value = true
            },
            onFilterClicked = { date ->
                viewModel.setDate(date)
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

        val context = LocalContext.current

        CustomAlertDialog(
            title = "Delete all diaries dialog",
            message = "Are you sure you want to permanently delete all diaries?",
            isDialogOpened = openDeletionDialogState,
            onYesClicked = {
                viewModel.deleteAllDiaries(
                    onSuccess = {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            navDrawerState.close()
                        }
                    },
                    onError = { ex ->
                        Toast.makeText(context, "${ex.message}", Toast.LENGTH_LONG).show()
                        scope.launch {
                            navDrawerState.close()
                        }
                    })
            },
            onDialogClosed = {
                openDeletionDialogState.value = false
            }
        )
    }
}