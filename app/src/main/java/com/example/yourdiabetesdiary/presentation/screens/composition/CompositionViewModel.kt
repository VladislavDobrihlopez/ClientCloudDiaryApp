package com.example.yourdiabetesdiary.presentation.screens.composition

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.yourdiabetesdiary.navigation.Screen

class CompositionViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = mutableStateOf(CompositionScreenState())
    val uiState: State<CompositionScreenState>
        get() = _uiState

    init {
        putRoutedDiaryIdArgument()
    }

    private fun putRoutedDiaryIdArgument() {
        _uiState.value =
            CompositionScreenState(selectedDiaryEntryId = savedStateHandle.get<String>(Screen.DiaryEntry.DIARY_ID_ARGUMENT_KEY))
    }
}