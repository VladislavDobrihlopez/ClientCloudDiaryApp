package com.example.yourdiabetesdiary.presentation.screens.composition

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.data.repository.MongoDbDbRepositoryImpl
import com.example.yourdiabetesdiary.domain.RequestState
import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.yourdiabetesdiary.models.GalleryItem
import com.example.yourdiabetesdiary.models.Mood
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.presentation.components.custom_states.GalleryState
import com.example.yourdiabetesdiary.util.toInstant
import com.example.yourdiabetesdiary.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.ZonedDateTime

class CompositionViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = mutableStateOf(CompositionScreenState())
    val uiState: State<CompositionScreenState>
        get() = _uiState

    private val _galleryState = mutableStateOf(GalleryState())
    val galleryState: State<GalleryState> = _galleryState

    init {
        putRoutedDiaryIdArgument()
        getDiaryInfo()
    }

    fun getDiaryInfo() {
        viewModelScope.launch {
            if (isInEditMode()) {
                val id = _uiState.value.selectedDiaryEntryId
                MongoDbDbRepositoryImpl.pullDiary(org.mongodb.kbson.ObjectId(id!!))
                    .catch { ex ->
                        emit(RequestState.Error(IllegalStateException("Diary already deleted")))
                    }
                    .collect { requestResult ->
                        if (requestResult is RequestState.Success) {
                            Log.d("TEST_DIARY", "mood: ${requestResult.data.mood}")

                            with(requestResult.data) {
                                setNewDate(date = date.toInstant())
                                setNewTitle(title = title)
                                setNewDescription(description = description)
                                setNewMood(mood = mood)
                            }
                        }
                    }
            }
        }
    }

    // forms the remote path for the photo in the following way: images/{user_id}/{photo_name}-{photo_id}.{extension}
    fun addImage(uri: Uri, imageType: String) {
        val remotePath =
            "images/${FirebaseAuth.getInstance().currentUser?.uid}/${uri.lastPathSegment}-${System.currentTimeMillis()}.${imageType}"
        Log.d("ADD_IMAGE", "addImage: $remotePath")
        _galleryState.value = GalleryState.setupImagesBasedOnPrevious(galleryState.value).apply {
            addImage(GalleryItem(remotePath = remotePath, localUri = uri))
        }
    }

    fun uploadImages() {
        with(FirebaseStorage.getInstance().reference) {
            galleryState.value.images.forEach { image ->
                Log.d("TEST_STORING", "a new to upload: $image")
                val meta = child(image.remotePath)
                meta.putFile(image.localUri)
                    .addOnFailureListener { ex ->
                        Log.d("TEST_STORING", "loading error: $ex")
                    }
                    .addOnSuccessListener {
                        Log.d("TEST_STORING", "success loading")
                    }
            }
        }
    }

    fun storeDiary(diary: DiaryEntry, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            MongoDbDbRepositoryImpl.upsertEntry(diary.apply {
                _uiState.value.date?.let { updatedOrScreenOpeningTime ->
                    this.date = updatedOrScreenOpeningTime.toRealmInstant()
                }
            }).collect { result ->
                Log.d("TEST_STORING", "$result")
                when (result) {
                    is RequestState.Success -> {
                        uploadImages()
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }

                    is RequestState.Error -> withContext(Dispatchers.Main) {
                        onFailure(result.ex.message.toString())
                    }

                    else -> onFailure("Unexpected problem occurred")
                }
            }
        }
    }

    fun deleteDiary(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            MongoDbDbRepositoryImpl.deleteDiary(diaryId = ObjectId(uiState.value.selectedDiaryEntryId!!))
                .collect { result ->
                    Log.d("TEST_DELETING", "$result")
                    when (result) {
                        is RequestState.Success ->
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }

                        is RequestState.Error ->
                            withContext(Dispatchers.Main) {
                                onFailure(result.ex.message.toString())
                            }

                        else -> onFailure("Unexpected problem occurred")
                    }
                }
        }
    }

    fun setNewTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun setNewDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun setNewMood(mood: String) {
        _uiState.value = _uiState.value.copy(mood = Mood.valueOf(mood))
    }

    fun setNewDate(date: Instant) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun setNewDateAndTime(zonedDateTime: ZonedDateTime) {
        _uiState.value = _uiState.value.copy(date = zonedDateTime.toInstant())
    }

    private fun putRoutedDiaryIdArgument() {
        _uiState.value =
            CompositionScreenState(selectedDiaryEntryId = savedStateHandle.get<String>(Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY))
    }

    private fun isInEditMode() = _uiState.value.selectedDiaryEntryId != null
}