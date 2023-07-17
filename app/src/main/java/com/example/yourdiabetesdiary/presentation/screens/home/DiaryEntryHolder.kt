package com.example.yourdiabetesdiary.presentation.screens.home

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.util.models.DiaryEntry
import com.example.util.models.Mood
import com.example.ui.components.Gallery
import com.example.ui.theme.Elevation
import com.example.yourdiabetesdiary.util.retrieveImagesFromFirebaseStorage
import com.example.yourdiabetesdiary.util.toInstant
import io.realm.kotlin.ext.realmListOf
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

private val DEFAULT_LINE_HEIGHT = 14.dp

@Composable
fun DiaryEntryHolder(entry: com.example.util.models.DiaryEntry, onClick: (String) -> Unit) {
    val localDensity = LocalDensity.current
    val context = LocalContext.current
    val componentHeight = remember {
        mutableStateOf(DEFAULT_LINE_HEIGHT)
    }
    val galleryOpened = rememberSaveable {
        mutableStateOf(false)
    }

    val galleryInLoadingState = remember {
        mutableStateOf(false)
    }

    val downloadedImagesState = remember {
        mutableStateListOf<Uri>()
    }

    LaunchedEffect(key1 = galleryOpened.value) {
        if (!galleryOpened.value) return@LaunchedEffect

        if (downloadedImagesState.isEmpty()) {
            galleryInLoadingState.value = true
            retrieveImagesFromFirebaseStorage(
                imagesUrls = entry.images,
                onCompletedDownloadingItem = { uri ->
                    downloadedImagesState.add(uri)
                },
                onFailure = { ex ->
                    Log.d("retrieveImagesFromFirebaseStorage", "onFailure: ${ex.message}")
                    Toast.makeText(context, "Error when loading images", Toast.LENGTH_LONG)
                        .show()
                    galleryInLoadingState.value = false
                    galleryOpened.value = false
                },
                onWholeWorkCompleted = {
                    galleryInLoadingState.value = false
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onClick(entry._id.toHexString())
            }
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Surface(
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight.value + DEFAULT_LINE_HEIGHT),
            tonalElevation = Elevation.level0
        ) {}
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    with(localDensity) {
                        componentHeight.value = it.size.height.toDp()
                    }
                },
            tonalElevation = Elevation.level1
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DiaryHeader(diary = entry, time = entry.date.toInstant())
                Text(
                    modifier = Modifier.padding(14.dp),
                    text = entry.description,
                    maxLines = 5,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                    overflow = TextOverflow.Ellipsis
                )
                if (entry.images.isNotEmpty()) {
                    ShouldDisplayOnOffSwitch(
                        isDisplayed = galleryOpened,
                        onClick = {
                            galleryOpened.value = !galleryOpened.value
                        }
                    )
                    AnimatedVisibility(
                        visible = galleryOpened.value,
                        enter = fadeIn() +
                                expandVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                    ) {
                        Column(modifier = Modifier.padding(all = 14.dp)) {
                            Gallery(images = downloadedImagesState.map { it.toString() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShouldDisplayOnOffSwitch(isDisplayed: State<Boolean>, onClick: () -> Unit) {
    TextButton(
        onClick = {
            onClick()
        }
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = if (isDisplayed.value) "Show less" else "Show more",
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        )
    }
}

@Composable
private fun DiaryHeader(diary: com.example.util.models.DiaryEntry, time: Instant) {
    val entry = com.example.util.models.Mood.valueOf(diary.mood)
    val containerColor by remember { mutableStateOf(entry.containerColor) }
    val contentColor by remember { mutableStateOf(entry.contentColor) }
    val iconResId by remember { mutableStateOf(entry.icon) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = containerColor)
            .padding(vertical = 7.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(18.dp),
                imageVector = ImageVector.vectorResource(id = iconResId),
                contentDescription = entry.name
            )
            Spacer(modifier = Modifier.padding(7.dp))
            Text(
                text = entry.name,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                ),
                color = contentColor
            )
        }
        Text(
            text = SimpleDateFormat("hh:mm a", Locale.UK).format(Date.from(time)),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
            color = contentColor
        )
    }
}

@Preview()
@Composable
fun DiaryEntryHolderPreview() {
    DiaryEntryHolder(entry = com.example.util.models.DiaryEntry().apply {
        title = "breakfast"
        description = "I've eaten 5 bread units in the morning, injured 12 units of novorapid"
        mood = com.example.util.models.Mood.Calm.name
        images = realmListOf("", "")
    }, onClick = {})
}