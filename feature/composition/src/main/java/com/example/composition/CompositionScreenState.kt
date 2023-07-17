package com.example.composition

import android.net.Uri
import com.example.util.models.Mood
import java.time.Instant

internal data class CompositionScreenState(
    val selectedDiaryEntryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val date: Instant? = null,
    val imagesUrl: List<Uri> = listOf()
)
