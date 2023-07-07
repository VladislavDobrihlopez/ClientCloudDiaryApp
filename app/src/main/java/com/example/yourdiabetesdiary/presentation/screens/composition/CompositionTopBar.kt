package com.example.yourdiabetesdiary.presentation.screens.composition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompositionTopBar(
    instant: Instant?,
    mood: () -> String,
    onNavigateBackClicked: () -> Unit,
    onDateRangeClicked: () -> Unit,
    availableActions: @Composable RowScope.() -> Unit
) {
    val currentDate = remember {
        mutableStateOf(LocalDate.now())
    }
    val currentTime = remember {
        mutableStateOf(LocalTime.now())
    }

    val selectedDiaryDateTime = remember(instant) {
        if (instant != null) {
            SimpleDateFormat(
                "dd MMM yyyy hh:mm",
                Locale.getDefault()
            ).format(Date.from(instant)).uppercase()
        } else {
            "${
                DateTimeFormatter.ofPattern("dd MMM yyyy").format(currentDate.value)
            }, ${DateTimeFormatter.ofPattern("hh:mm").format(currentTime.value)}"
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
            IconButton(onClick = onDateRangeClicked) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Set up the date",
                    tint = MaterialTheme.colorScheme.onSurface
                )
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
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    )
}

@Preview
@Composable
fun CompositionTopAppBarPreview() {
    YourDiabetesDiaryTheme {
        CompositionTopBar(
            onNavigateBackClicked = {},
            availableActions = {},
            onDateRangeClicked = {},
            instant = null,
            mood = { "Normal mood" })
    }
}