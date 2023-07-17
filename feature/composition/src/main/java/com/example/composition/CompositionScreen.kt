package com.example.composition

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.window.Dialog
import com.example.ui.components.GalleryItem
import com.example.ui.components.CustomAlertDialog
import com.example.ui.components.ZoomableImage
import com.example.ui.components.custom_states.GalleryState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import java.time.Instant
import java.time.ZonedDateTime

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CompositionScreen(
    mood: () -> String,
    pagerState: PagerState,
    screenState: CompositionScreenState,
    date: Instant?,
    navigateBack: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    galleryState: State<GalleryState>,
    onDeleteConfirmed: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveDiaryButtonClicked: (com.example.util.models.DiaryEntry) -> Unit,
    onDateUpdated: (ZonedDateTime) -> Unit,
    onDeleteGalleryImage: (GalleryItem) -> Unit
) {
    val zoomableImage = remember {
        mutableStateOf<GalleryItem?>(null)
    }

    Scaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            CompositionTopBar(
                instant = date,
                mood = mood,
                onNavigateBackClicked = {
                    navigateBack()
                },
                onDateUpdated = onDateUpdated,
                availableActions = {
                    if (date == null) {
                        return@CompositionTopBar
                    }

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
        CompositionContent(
            pagerState = pagerState,
            paddingValues = paddings,
            title = screenState.title,
            onImageSelected = onImageSelected,
            galleryState = galleryState,
            onTitleChanged = { newTitle -> onTitleChanged(newTitle) },
            description = screenState.description,
            onDescriptionChanged = { newDescription -> onDescriptionChanged(newDescription) },
            onGalleryImageClicked = { galleryItem ->
                zoomableImage.value = galleryItem
            },
            onSaveDiaryButtonClicked = { diary ->
                onSaveDiaryButtonClicked(diary)
            }
        )

        AnimatedVisibility(visible = zoomableImage.value != null) {
            Dialog(onDismissRequest = {
                zoomableImage.value = null
            }) {
                zoomableImage.value?.let {
                    ZoomableImage(
                        url = it.localUri,
                        onCloseClicked = {
                            zoomableImage.value = null
                        },
                        onDeleteClicked = {
                            onDeleteGalleryImage(it)
                            zoomableImage.value = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ConfirmationDialog(
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
        title = performedAction.name.lowercase().replaceFirstChar { it.titlecase() },
        message = "Are you sure you want to ${performedAction.name.lowercase()} this diary without the possibility to restore in the future?",
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
internal fun DropDownOptions(
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

internal enum class DropDownMenuOptions {
    DELETE
}