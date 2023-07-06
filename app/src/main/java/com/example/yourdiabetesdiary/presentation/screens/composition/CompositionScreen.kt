package com.example.yourdiabetesdiary.presentation.screens.composition

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.yourdiabetesdiary.models.DiaryEntry
import com.example.yourdiabetesdiary.presentation.components.CustomAlertDialog

@Composable
fun CompositionScreen(
    diaryEntry: DiaryEntry?,
    navigateBack: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    Scaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            CompositionTopBar(
                onNavigateBackClicked = {
                    navigateBack()
                },
                onDateRangeClicked = {

                },
                availableActions = {
//                    if (diaryEntry == null) {
//                        return@CompositionTopBar
//                    }

                    val isDialogOpened = remember {
                        mutableStateOf(false)
                    }

                    val chosenDropDownMenuOption = remember {
                        mutableStateOf<DropDownMenuOptions?>(null)
                    }

                    var isDropMenuVisible by remember {
                        mutableStateOf(false)
                    }

                    IconButton(onClick = {
                        isDropMenuVisible = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Extra options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropDownOptions(
                        expanded = isDropMenuVisible,
                        onDismiss = { isDropMenuVisible = false },
                        onActionChose = { option ->
                            when (option) {
                                DropDownMenuOptions.DELETE -> {
                                    isDialogOpened.value = true
                                }
                            }
                            chosenDropDownMenuOption.value = option
                        }
                    )

                    ConfirmationDialog(
                        isDialogOpened = isDialogOpened,
                        action = chosenDropDownMenuOption,
                        showOff = {
                            isDialogOpened.value = false
                        },
                        onConfirmation = { option ->
                            when (option) {
                                DropDownMenuOptions.DELETE -> onDeleteConfirmed()
                            }
                        }
                    )
                }
            )
        }
    ) { paddings ->
        CompositionContent(paddings)
    }
}

@Composable
fun ConfirmationDialog(
    isDialogOpened: State<Boolean>,
    action: State<DropDownMenuOptions?>,
    showOff: () -> Unit,
    onConfirmation: (DropDownMenuOptions) -> Unit
) {
    if (!isDialogOpened.value) {
        return
    }

    val performedAction = if (isDialogOpened.value) {
        action.value!!
    } else {
        throw IllegalStateException("Action is not allowed to be null as action confirmation dialog is displayed")
    }

    CustomAlertDialog(
        title = "${performedAction}",
        message = "Are you sure you want to ${performedAction} this diary without the possibility to restore in the future?",
        isDialogOpened = isDialogOpened,
        onYesClicked = {
            showOff()
            onConfirmation(performedAction)
        },
        onDialogClosed = {
            showOff()
        }
    )
}

@Composable
fun DropDownOptions(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onActionChose: (DropDownMenuOptions) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = {
        onDismiss()
    }) {
        DropdownMenuItem(
            text = { Text(text = "Delete") },
            onClick = { onActionChose(DropDownMenuOptions.DELETE) })
    }
}

enum class DropDownMenuOptions {
    DELETE
}