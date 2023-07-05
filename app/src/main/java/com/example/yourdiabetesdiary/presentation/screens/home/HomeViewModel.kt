package com.example.yourdiabetesdiary.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.data.repository.DiariesType
import com.example.yourdiabetesdiary.data.repository.MongoDbDbRepositoryImpl
import com.example.yourdiabetesdiary.domain.RequestState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val diaries: MutableState<RequestState<DiariesType>> = mutableStateOf(RequestState.Idle)

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            MongoDbDbRepositoryImpl.retrieveDiaries().collect { result ->
                Log.d("TEST_DIARY", result.toString())
                diaries.value = result
            }
        }
    }
}