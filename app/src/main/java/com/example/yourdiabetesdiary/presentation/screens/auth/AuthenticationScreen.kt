package com.example.yourdiabetesdiary.presentation.screens.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.yourdiabetesdiary.util.Constants
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    oneTapState: OneTapSignInState,
    authResultState: MessageBarState,
    onButtonClick: () -> Unit,
    loadingState: Boolean
) {
    Scaffold(content = {
        ContentWithMessageBar(messageBarState = authResultState) {
            AuthenticationContent(onClick = onButtonClick, loadingState = loadingState)
        }
    })

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = Constants.GOOGLE_CLOUD_CLIENT_ID,
        onTokenIdReceived = { token ->
            Log.d("AUTH", token)
            authResultState.addSuccess("Successfully authorized")
    }, onDialogDismissed = { message ->
            Log.d("AUTH", message)
            authResultState.addError(Exception(message))
        })
}