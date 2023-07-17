package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Elevation

@Composable
internal fun AddMoreButton(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    shape: CornerBasedShape,
    onClicked: () -> Unit
) {
    Surface(
        modifier = modifier
            .size(size)
            .clip(shape),
        onClick = onClicked,
        tonalElevation = Elevation.level1
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add a new image to the diary")
        }
    }
}
