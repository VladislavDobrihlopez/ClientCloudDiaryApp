package com.example.yourdiabetesdiary.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.ui.components.DateHeader
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    diariesOnSpecificDate: Map<LocalDate, List<DiaryEntry>>,
    onDiaryClick: (String) -> Unit
) {
    if (diariesOnSpecificDate.isNotEmpty()) {
        LazyColumn(modifier = modifier.padding(24.dp)) {
            diariesOnSpecificDate.forEach { date, diaries ->
                stickyHeader(key = date) {
                    DateHeader(localDate = date)
                }

                items(
                    items = diaries,
                    key = { it._id.toString() }
                ) { diary ->
                    DiaryEntryHolder(entry = diary, onClick = onDiaryClick)
                }
            }
        }
    } else {
        EmptyDataInfo()
    }
}

@Composable
fun EmptyDataInfo(
    title: String = "No diaries yet",
    subtitle: String = "Do you mind adding a new one? Tap on the bottom-right button"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = title,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            textAlign = TextAlign.Center,
            text = subtitle,
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Normal
            )
        )
    }
}