package com.example.yourdiabetesdiary.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.util.Constants
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {
    var authenticationState = mutableStateOf(false)
        private set
    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun signInWithMongoAtlas(
        token: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    App.Companion.create(Constants.MONGO_DB_APP_ID)
                        .login(Credentials.jwt(token)).loggedIn
                }
                if (result) {
                    setLoading(true)
                    onSuccess()
                    delay(500)
                    authenticationState.value = true
                } else {
                    onError(Exception("Smth went wrong. User is logged in"))
                }
            } catch (ex: Exception) {
                authenticationState.value = false
                setLoading(false)
                onError(ex)
            }
        }
    }
}