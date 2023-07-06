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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompositionTopBar(
    onNavigateBackClicked: () -> Unit,
    onDateRangeClicked: () -> Unit,
    availableActions: @Composable RowScope.() -> Unit
) {
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
                    text = "Chill",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Jun 19, 23:00",
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
        CompositionTopBar(onNavigateBackClicked = {}, availableActions = {}, onDateRangeClicked = {})
    }
}