package com.example.roomlibrary.component
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roomlibrary.ui.theme.TagColor

@Composable
fun CategoryTag(tagName: String) {
        Text(
            text = tagName,
            modifier = Modifier
                .background(Color(0xFFd1e8ff), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp)
            ,
            style = MaterialTheme.typography.bodyMedium.copy(

            ),
            color = TagColor,
            fontWeight = FontWeight.Bold
        )
}