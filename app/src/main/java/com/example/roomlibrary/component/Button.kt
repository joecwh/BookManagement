package com.example.roomlibrary.component
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.roomlibrary.ui.theme.DangerColor
import com.example.roomlibrary.ui.theme.LightGrayColor

@Composable
fun DangerButton(
    text: String,
    onClicked: () -> Unit
) {
    Button(
        onClick = onClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = DangerColor
        )
    ) {
        Text(text)
    }
}

@Composable
fun InfoButton(
    text: String,
    textColor: Color,
    onClicked: () -> Unit
) {
    Button(
        onClick = onClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = LightGrayColor
        )
    ) {
        Text(text, color = textColor)
    }
}