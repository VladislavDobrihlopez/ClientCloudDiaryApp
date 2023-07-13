package com.example.yourdiabetesdiary.presentation.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.yourdiabetesdiary.models.GalleryItem
import com.example.yourdiabetesdiary.presentation.components.custom_states.GalleryState

@Composable
fun GalleryUploader(
    modifier: Modifier = Modifier,
    state: State<GalleryState>,
    imageSize: Dp = 56.dp,
    spacedBy: Dp = 8.dp,
    maxItemsSelectedAtMoment: Int = 3, // must be >= 2
    imageShape: CornerBasedShape = Shapes().medium,
    onImageAdd: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryItem) -> Unit,
) {

    Log.d("TEST_IMAGE_SELECTION", "recomposition outer: ${state.value}\n ${state.value.images}")

    val multiplePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItemsSelectedAtMoment),
        onResult = { uris ->
            uris.forEach {
                onImageSelected(it)
            }
        }
    )

    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                Integer.max(0, this.maxWidth.div(spacedBy + imageSize).toInt().minus(2))
            }
        }
        val numberOfRemainingImages = remember {
            derivedStateOf {
                Log.d("TEST_IMAGE_SELECTION", "recomposition: ${state.value.images.count()}")
                state.value.images.count() - numberOfVisibleImages.value
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AddMoreButton(size = imageSize, shape = imageShape, onClicked = {
                onImageAdd()
                multiplePicker.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            })

            Spacer(modifier = Modifier.width(spacedBy))

//            LaunchedEffect(key1 = state.value.images.count(), key2 = state.value.imagesForDeletion.count()) {
//                Log.d("TEST_IMAGE_SELECTION", "recomposition: launched effect")
//                items.value = state.value.images.toList()
//            }
            state.value.images.take(numberOfVisibleImages.value).forEachIndexed { index, galleryItem ->
                Log.d("TEST_IMAGE_SELECTION", "recomposition: ${state.value.images.count()} foreach")
                AsyncImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(imageShape)
                        .clickable {
                            onImageClicked(galleryItem)
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(galleryItem.localUri)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Image №$index"
                )
                Spacer(modifier = Modifier.width(spacedBy))
            }

            if (numberOfRemainingImages.value > 0) {
                NumberOfNotFittedImages(
                    imageSize = imageSize,
                    imageShape = imageShape,
                    numberOfRemainingImages = numberOfRemainingImages
                )
            }
        }
    }
}