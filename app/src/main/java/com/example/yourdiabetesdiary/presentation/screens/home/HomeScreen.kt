package com.example.yourdiabetesdiary.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.yourdiabetesdiary.R
import com.example.yourdiabetesdiary.domain.DiariesType
import com.example.yourdiabetesdiary.domain.RequestState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    state: RequestState<DiariesType>,
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToCompositionScreen: () -> Unit,
    onSignOut: () -> Unit,
    onDiaryChose: (String) -> Unit,
    onDeleteAllDiariesClicked: () -> Unit,
    onFilterClicked: (LocalDate?) -> Unit
) {
    var padding by remember {
        mutableStateOf(PaddingValues())
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    NavigationDrawer(
        drawerState = drawerState,
        onSignOut = {
            onSignOut()
        },
        onAboutClicked = {},
        onDeleteAllDiariesClicked = onDeleteAllDiariesClicked
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onNavigationMenuClicked = { onMenuClicked() },
                    onFilterClicked = onFilterClicked)
            }, floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(
                        end = padding.calculateEndPadding(
                            LayoutDirection.Ltr
                        )
                    ), onClick = { navigateToCompositionScreen() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add a note")
                }
            }, content = {
                padding = it
                when (val currentState = state) {
                    is RequestState.Error -> {
                        EmptyDataInfo(
                            title = "Error occurred",
                            subtitle = currentState.ex.message.toString()
                        )
                    }

                    RequestState.Idle -> {

                    }

                    RequestState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is RequestState.Success -> {
                        HomeContent(
                            modifier = Modifier.padding(it),
                            diariesOnSpecificDate = currentState.data,
                            onDiaryClick = onDiaryChose
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOut: () -> Unit,
    onAboutClicked: () -> Unit,
    onDeleteAllDiariesClicked: () -> Unit,
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
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete all diaries"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Delete all diaries",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    selected = false,
                    onClick = { onDeleteAllDiariesClicked() }
                )
                Spacer(modifier = Modifier.height(12.dp))
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