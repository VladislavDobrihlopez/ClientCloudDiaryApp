package com.example.yourdiabetesdiary.presentation.screens.auth

import android.annotation.SuppressLint
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
    onTokenReceived: (String) -> Unit,
    onReceivingDismissed: (String) -> Unit,
    loadingState: Boolean
) {
    Scaffold(content = {
        ContentWithMessageBar(
            messageBarState = authResultState,
            errorMaxLines = 3,
            successMaxLines = 2,
            visibilityDuration = 5000L
        ) {
            AuthenticationContent(onClick = onButtonClick, loadingState = loadingState)
        }
    })

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = Constants.GOOGLE_CLOUD_CLIENT_ID,
        onTokenIdReceived = { token ->
            onTokenReceived(token)
        }, onDialogDismissed = { message ->
            onReceivingDismissed(message)
        })
}