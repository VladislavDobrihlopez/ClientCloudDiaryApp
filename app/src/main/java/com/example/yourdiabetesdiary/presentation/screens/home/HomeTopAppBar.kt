package com.example.yourdiabetesdiary.presentation.screens.home

import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationMenuClicked: () -> Unit,
    onFilterClicked: () -> Unit
) {
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
        IconButton(onClick = onFilterClicked) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date picker",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    })
}