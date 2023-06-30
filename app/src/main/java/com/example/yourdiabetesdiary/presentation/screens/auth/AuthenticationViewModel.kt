package com.example.yourdiabetesdiary.presentation.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.util.Constants
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {
    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun signInWithMongoAtlas(
        token: String,
        onSuccess: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    App.Companion.create(Constants.MONGO_DB_APP_ID)
                        .login(
//                            Credentials.google(
//                                token = token,
//                                type = GoogleAuthType.ID_TOKEN
//                            )
                        Credentials.jwt(token)
                        ).loggedIn
                }
                //setLoading(true)
                onSuccess(result)
            } catch (ex: Exception) {
                //setLoading(false)
                onError(ex)
            }
        }
    }
}