package com.example.yourdiabetesdiary.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.data.database.dao.ImageInQueryForDeletionDao
import com.example.yourdiabetesdiary.data.database.models.ImagesForDeletionDbModel
import com.example.yourdiabetesdiary.domain.ConnectivityObserver
import com.example.yourdiabetesdiary.domain.DiariesType
import com.example.yourdiabetesdiary.domain.MongoDbRepository
import com.example.yourdiabetesdiary.domain.RequestState
import com.example.yourdiabetesdiary.domain.exceptions.CustomException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: ConnectivityObserver,
    private val remoteDb: MongoDbRepository,
    private val pendingImagesForDeletionDao: ImageInQueryForDeletionDao
) : ViewModel() {
    val diaries: MutableState<RequestState<DiariesType>> = mutableStateOf(RequestState.Idle)
    private val connectivityStatus =
        mutableStateOf(ConnectivityObserver.Status.LOST)
    private val selectedDate = mutableStateOf<LocalDate?>(null)

    init {
        observeDiaries()
        observeConnectivity()
    }

    fun setDate(date: LocalDate?) {
        selectedDate.value = date
        observeDiaries()
    }

    private fun observeDiaries() {
        viewModelScope.launch {
            val date = selectedDate.value
            if (date == null) {
                remoteDb.retrieveDiaries().collect { result ->
                    Log.d("TEST_DIARY", "1: $result")
                    diaries.value = result
                }
            } else {
                remoteDb.retrieveFilteredDiaries(date).collect { result ->
                    Log.d("TEST_DIARY", "2: $result`")
                    diaries.value = result
                }
            }
        }
    }

    fun observeConnectivity() {
        viewModelScope.launch {
            connectivity.observe().collect { incomingStatus ->
                connectivityStatus.value = incomingStatus
            }
        }
    }

    fun deleteAllDiaries(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        if (connectivityStatus.value == ConnectivityObserver.Status.AVAILABLE) {
            val storageReference = FirebaseStorage.getInstance().reference
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                onError(CustomException.UserNotAuthenticatedException())
            }
            storageReference.child("images/${userId}")
                .listAll()
                .addOnSuccessListener { images ->
                    images.items.forEach { image ->
                        Log.d("TEST_DELETE_ALL", "image: $image")
                        storageReference.child(image.path).delete()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { ex ->
                                viewModelScope.launch(Dispatchers.IO) {
                                    pendingImagesForDeletionDao.addImage(
                                        model = ImagesForDeletionDbModel(
                                            remotePath = image.path
                                        )
                                    )
                                }
                                onError(ex)
                            }
                    }
                }
                .addOnFailureListener(onError)

            viewModelScope.launch(Dispatchers.IO) {
                val result = remoteDb.deleteAllDiaries()
                Log.d("TEST_DELETE_ALL", result.toString())
            }
        } else {
            onError(CustomException.NoInternetConnection())
        }
    }
}