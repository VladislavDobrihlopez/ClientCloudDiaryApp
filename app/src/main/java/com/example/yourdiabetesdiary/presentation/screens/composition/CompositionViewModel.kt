package com.example.yourdiabetesdiary.presentation.screens.composition

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourdiabetesdiary.data.repository.MongoDbDbRepositoryImpl
import com.example.yourdiabetesdiary.domain.RequestState
import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.yourdiabetesdiary.models.Mood
import com.example.yourdiabetesdiary.navigation.Screen
import com.example.yourdiabetesdiary.util.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class CompositionViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = mutableStateOf(CompositionScreenState())
    val uiState: State<CompositionScreenState>
        get() = _uiState

    init {
        putRoutedDiaryIdArgument()
        getDiaryInfo()
    }

    fun getDiaryInfo() {
        viewModelScope.launch {
            if (isInEditMode()) {
                val id = _uiState.value.selectedDiaryEntryId

                val requestResult =
                    MongoDbDbRepositoryImpl.pullDiary(org.mongodb.kbson.ObjectId(id!!))


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

    fun storeDiary(diary: DiaryEntry, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = MongoDbDbRepositoryImpl.addNewDiary(diary)) {
                is RequestState.Success -> {
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

    private fun putRoutedDiaryIdArgument() {
        _uiState.value =
            CompositionScreenState(selectedDiaryEntryId = savedStateHandle.get<String>(Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY))
    }

    private fun isInEditMode() = _uiState.value.selectedDiaryEntryId != null
}