package com.example.yourdiabetesdiary.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.yourdiabetesdiary.ui.theme.Elevation
import java.lang.Integer.max

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<String>,
    imageSize: Dp = 40.dp,
    spacedBy: Dp = 10.dp,
    imageShape: CornerBasedShape = Shapes().small,
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(0, this.maxWidth.div(spacedBy + imageSize).toInt().minus(1))
            }
        }
        val numberOfRemainingImages = remember {
            derivedStateOf {
                images.count() - numberOfVisibleImages.value
            }
        }

        images.take(numberOfVisibleImages.value).forEachIndexed { index, url ->
            AsyncImage(
                modifier = Modifier
                    .size(imageSize)
                    .clip(imageShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(url)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Image №$index"
            )
            Spacer(modifier = Modifier.width(spacedBy))
        }

        if (numberOfRemainingImages.value > 0) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(imageShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    tonalElevation = Elevation.level2
                ) {
                    Text(
                        text = "+$numberOfRemainingImages",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}