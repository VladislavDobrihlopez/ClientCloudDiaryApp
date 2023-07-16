package com.example.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    isDialogOpened: State<Boolean>,
    onYesClicked: () -> Unit,
    onDialogClosed: () -> Unit
) {
    if (isDialogOpened.value) {
        AlertDialog(
            onDismissRequest = {
                onDialogClosed()
            },
            confirmButton = {
                Button(onClick = {
                    onYesClicked()
                    onDialogClosed()
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onDialogClosed() }) {
                    Text(text = "Stay here")
                }
            },
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal
                )
            },
        )
    }
}