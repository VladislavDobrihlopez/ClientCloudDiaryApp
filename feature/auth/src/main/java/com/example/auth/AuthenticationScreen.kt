package com.example.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun AuthenticationScreen(
    authenticated: Boolean,
    oneTapState: OneTapSignInState,
    authResultState: MessageBarState,
    loadingState: Boolean,
    onScreenIsReady: () -> Unit,
    onButtonClick: () -> Unit,
    onSuccessfulFirebaseSignIn: (String) -> Unit, // token
    onFailedSignIn: (Exception) -> Unit,
    onReceivingDismissed: (String) -> Unit,
    navigateToHome: () -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onScreenIsReady()
    }
    Scaffold(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .statusBarsPadding(),
        content = {
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
            val credential = GoogleAuthProvider.getCredential(token, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        onSuccessfulFirebaseSignIn(token)
                    } else {
                        result.exception?.let { onFailedSignIn(it) }
                    }
                }
        }, onDialogDismissed = { message ->
            onReceivingDismissed(message)
        })

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHome()
        }
    }
}