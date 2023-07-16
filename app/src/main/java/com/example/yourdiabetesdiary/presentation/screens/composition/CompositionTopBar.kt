package com.example.yourdiabetesdiary.presentation.screens.composition

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.YourDiabetesDiaryTheme
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompositionTopBar(
    instant: Instant?,
    mood: () -> String,
    onNavigateBackClicked: () -> Unit,
    onDateUpdated: (ZonedDateTime) -> Unit,
    availableActions: @Composable RowScope.() -> Unit
) {
    val currentDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val currentTime = remember {
        mutableStateOf(LocalTime.now())
    }
    val hasBeenUpdatedDuringScreenSession = remember {
        mutableStateOf(false)
    }
    val formattedDate = remember(currentDate.value) {
        mutableStateOf(DateTimeFormatter.ofPattern("dd MMM yyyy").format(currentDate.value))
    }
    val formattedTime = remember(currentTime.value) {
        mutableStateOf(DateTimeFormatter.ofPattern("hh:mm a").format(currentTime.value))
    }

    Log.d("NEW_STATE", "compose: ${instant} ${currentTime.value} ${currentDate.value}")

    val dateMenu = rememberSheetState()
    val timeMenu = rememberSheetState()

    val selectedDiaryDateTime = remember(instant, hasBeenUpdatedDuringScreenSession.value) {
        if (instant != null && hasBeenUpdatedDuringScreenSession.value) {
            "${formattedDate.value}, ${formattedTime.value}"
        } else if (instant != null) {
            SimpleDateFormat(
                "dd MMM yyyy hh:mm a",
                Locale.getDefault()
            ).format(Date.from(instant)).uppercase()
        } else {
            "${formattedDate.value}, ${formattedTime.value}"
        }
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (!hasBeenUpdatedDuringScreenSession.value) {
                IconButton(onClick = { dateMenu.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Set up the date",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                IconButton(onClick = {
                    currentDate.value = LocalDate.now()
                    currentTime.value = LocalTime.now()
                    hasBeenUpdatedDuringScreenSession.value = false
                    onDateUpdated(
                        ZonedDateTime.of(
                            LocalDate.now(),
                            LocalTime.now(),
                            ZoneId.systemDefault()
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear the date",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            availableActions()
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mood(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = selectedDiaryDateTime,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    )

    CalendarDialog(state = dateMenu, selection = CalendarSelection.Date { newDate ->
        currentDate.value = newDate
        timeMenu.show()
    }, config = CalendarConfig(monthSelection = true, yearSelection = true))

    ClockDialog(state = timeMenu, selection = ClockSelection.HoursMinutes { hours, minutes ->
        currentTime.value = LocalTime.of(hours, minutes)
        hasBeenUpdatedDuringScreenSession.value = true
        onDateUpdated(
            ZonedDateTime.of(
                currentDate.value,
                currentTime.value,
                ZoneId.systemDefault()
            )
        )
    })
}

@Preview
@Composable
fun CompositionTopAppBarPreview() {
    YourDiabetesDiaryTheme {
        CompositionTopBar(
            onNavigateBackClicked = {},
            availableActions = {},
            onDateUpdated = {},
            instant = null,
            mood = { "Normal mood" })
    }
}