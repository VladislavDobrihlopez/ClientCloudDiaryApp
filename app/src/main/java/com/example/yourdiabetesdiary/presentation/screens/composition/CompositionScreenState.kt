package com.example.yourdiabetesdiary.presentation.screens.composition

import android.net.Uri
import com.example.util.models.Mood
import java.time.Instant

data class CompositionScreenState(
    val selectedDiaryEntryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: com.example.util.models.Mood = com.example.util.models.Mood.Neutral,
    val date: Instant? = null,
    val imagesUrl: List<Uri> = listOf()
)
