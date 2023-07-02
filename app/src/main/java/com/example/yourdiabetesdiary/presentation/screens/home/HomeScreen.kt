package com.example.yourdiabetesdiary.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yourdiabetesdiary.R
import com.example.yourdiabetesdiary.presentation.components.DateHeader
import java.time.LocalDate

@Composable
fun HomeScreen(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWriteScreen: () -> Unit,
    onSignOut: () -> Unit
) {
    NavigationDrawer(
        drawerState = drawerState,
        onSignOut = {
            onSignOut()
        },
        onAboutClicked = {}
    ) {
        Scaffold(topBar = {
            HomeTopAppBar(onNavigationMenuClicked = { onMenuClicked() }, onFilterClicked = { })
        }, floatingActionButton = {
            FloatingActionButton(onClick = { navigateToWriteScreen() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add a note")
            }
        }, content = {
           // DiaryEntryHolder()
        })
    }
}

@Composable
private fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOut: () -> Unit,
    onAboutClicked: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "app logo"
                    )
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "Diabetes diary app",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                NavigationDrawerItem(
                    label = {
                        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = "google logo"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Sign out", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    selected = false,
                    onClick = { onSignOut() }
                )
                NavigationDrawerItem(
                    label = {
                        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Default.Info,
                                contentDescription = "about"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "About", color = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    selected = false,
                    onClick = { onAboutClicked() }
                )
            }
        },
        content = content,
        drawerState = drawerState
    )
}