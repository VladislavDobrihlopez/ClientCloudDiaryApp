package com.example.yourdiabetesdiary.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yourdiabetesdiary.R
import com.example.yourdiabetesdiary.ui.theme.YourDiabetesDiaryTheme

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loadingState: Boolean = false,
    primaryText: String = "Sign in with Google",
    secondaryText: String = "Please wait...",
    icon: Int = R.drawable.google_logo,
    shape: Shape = Shapes().extraSmall,
    borderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderStrokeWidth: Dp = 2.dp,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    val statusText = remember {
        mutableStateOf(primaryText)
    }

    LaunchedEffect(key1 = loadingState) {
        statusText.value = if (loadingState) secondaryText else primaryText
    }

    Surface(
        modifier = modifier
            .clickable {
                if (!loadingState) {
                    onClick()
                }
            }
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = shape,
        border = BorderStroke(width = borderStrokeWidth, color = borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = icon),
                contentDescription = stringResource(R.string.content_description_google_logo),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = statusText.value,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            )
            if (loadingState) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp),
                    strokeWidth = 3.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewGoogleButton() {
    YourDiabetesDiaryTheme {
        GoogleButton {}
    }
}

@Preview
@Composable
private fun PreviewGoogleButton2() {
    YourDiabetesDiaryTheme {
        GoogleButton(loadingState = true) {}
    }
}