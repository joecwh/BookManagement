package com.example.roomlibrary.component
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.roomlibrary.ui.theme.BlackColor

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to perform this action?") },
            confirmButton = {

                DangerButton("Delete", onClicked = onConfirm)
            },
            dismissButton = {
                InfoButton("Cancel", BlackColor, onClicked = onDismiss)
            }
        )
    }
}