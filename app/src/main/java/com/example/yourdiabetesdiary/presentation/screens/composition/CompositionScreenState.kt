package com.example.yourdiabetesdiary.presentation.screens.composition

import com.example.yourdiabetesdiary.models.Mood

data class CompositionScreenState(
    val selectedDiaryEntryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)
