package com.example.yourdiabetesdiary.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yourdiabetesdiary.R
import com.example.yourdiabetesdiary.presentation.components.GoogleButton

@Composable
fun AuthenticationContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loadingState: Boolean
) {
    Column(
        modifier = modifier.padding(all = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(10f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = stringResource(R.string.content_description_google_logo)
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(R.string.auth_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                text = stringResource(R.string.auth_subtitle),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            GoogleButton(onClick = onClick, loadingState = loadingState)
        }
    }
}