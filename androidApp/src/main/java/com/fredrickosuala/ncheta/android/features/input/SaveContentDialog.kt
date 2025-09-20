package com.fredrickosuala.ncheta.android.features.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SaveContentDialog(
    title: String,
    onTitleChanged: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClicked: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Content Generated Successfully") },
        text = {
            Column {
                Text("Please provide a title for this entry.")
                OutlinedTextField(
                    value = title,
                    onValueChange = { onTitleChanged(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    label = { Text("Title") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSaveClicked,
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}