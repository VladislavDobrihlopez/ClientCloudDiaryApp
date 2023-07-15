package com.example.yourdiabetesdiary.presentation.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationMenuClicked: () -> Unit,
    onFilterClicked: (LocalDate?) -> Unit
) {
    val datePickerState = rememberSheetState()

    val isInFiltrationMode = rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
        IconButton(onClick = onNavigationMenuClicked) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Hamburger icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }, title = {
        Text(text = "Home screen")
    }, actions = {
        IconButton(onClick = {
            if (!isInFiltrationMode.value) {
                datePickerState.show()
            } else {
                isInFiltrationMode.value = false
                onFilterClicked(null)
            }
        }) {
            if (isInFiltrationMode.value) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear the date",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date picker",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    })

    CalendarDialog(state = datePickerState, selection = CalendarSelection.Date { date ->
        onFilterClicked(date)
        isInFiltrationMode.value = true
    }, config = CalendarConfig(monthSelection = true, yearSelection = true))
}