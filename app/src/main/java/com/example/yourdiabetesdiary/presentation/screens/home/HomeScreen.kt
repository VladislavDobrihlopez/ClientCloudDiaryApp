package com.example.yourdiabetesdiary.presentation.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onMenuClicked: () -> Unit, navigateToWriteScreen: () -> Unit) {
    Scaffold(topBar = {
        HomeTopAppBar(onNavigationMenuClicked = { onMenuClicked() }, onFilterClicked = { })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navigateToWriteScreen() }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add a note")
        }
    }, content = {})
}