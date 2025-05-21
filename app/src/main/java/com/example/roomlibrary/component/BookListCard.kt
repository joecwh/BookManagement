package com.example.roomlibrary.component
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.roomlibrary.service.entity.Book
import com.example.roomlibrary.ui.theme.LightGrayColor
import com.example.roomlibrary.ui.theme.YellowColor
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun InventoryItem(
    book: Book,
    onItemClick: () -> Unit,
    onDeleteItem: (Book) -> Unit
) {
    val decimalFormat = DecimalFormat("#,##0.00")  // Formatter for thousands separator
    val formattedPrice = decimalFormat.format(book.price)  // Convert price to formatted string

    SwipeToDeleteContainer(book, { onDeleteItem(book) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onItemClick() },
            colors = CardDefaults.cardColors(
                containerColor = LightGrayColor
            ),
            border = BorderStroke(2.dp, YellowColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Column(
                        modifier = Modifier.weight(0.7f)
                    ) {
                        CategoryTag(book.category)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = book.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Text(
                        text = "| ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Start
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .weight(0.1f)
                            .padding(vertical = 4.dp)
                    )
                    Text(
                        text = "RM ${formattedPrice}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.End
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteContainer(
    book: Book,
    onDeleteItem: (Book) -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = with(LocalDensity.current) { 80.dp.toPx() } // maximum drag distance
    val scope = rememberCoroutineScope() // drag track
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // delete box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red)
                .align(Alignment.CenterEnd)
                .padding(vertical = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 25.dp)
            )
        }
        // content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(offsetX.roundToInt(), 0)
                }
                .background(LightGrayColor)
                .draggable(
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (offsetX <= -maxOffset) {
                            showConfirmationDialog = true
                        }
                        scope.launch {
                            offsetX = 0f
                        }
                    },
                    state = rememberDraggableState { delta ->
                        offsetX = (offsetX + delta).coerceIn(-maxOffset, 0f)
                    }
                )
        ) {
            content()
        }

        if (showConfirmationDialog) {
            ConfirmationDialog(
                showDialog = showConfirmationDialog,
                onConfirm = {
                    onDeleteItem(book)
                    showConfirmationDialog = false // close dialog
                },
                onDismiss = {
                    showConfirmationDialog = false // close dialog
                }
            )
        }
    }
}