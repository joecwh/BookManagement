package com.example.roomlibrary.screen
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomlibrary.component.BookCategory
import com.example.roomlibrary.service.entity.Book
import com.example.roomlibrary.ui.theme.YellowColor
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
    navController: NavController,
    book: Book? = null,
    onSave: (Book) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(book?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(book?.category ?: BookCategory.FICTION.displayName) }
    var customCategory by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(book?.quantity?.toString() ?: "0") }
    var price by remember { mutableStateOf(book?.price?.toString() ?: "0.00") }
    val decimalFormat = DecimalFormat("#,##0.00") // Format with commas

    // Track invalid input attempts
    var showQuantityError by remember { mutableStateOf(false) }
    var showPriceError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // Dropdown state

    val isFormValid = name.isNotBlank() &&
            (selectedCategory != BookCategory.OTHER.displayName || customCategory.isNotBlank()) &&
            quantity.toIntOrNull() != null &&
            price.replace(",", "").toDoubleOrNull() != null

    // Show toast if invalid character is entered for quantity
    LaunchedEffect(showQuantityError) {
        if (showQuantityError) {
            Toast.makeText(context, "Only numbers are allowed for quantity", Toast.LENGTH_SHORT).show()
            showQuantityError = false // Reset flag after showing toast
        }
    }

    // Show toast if invalid price format is entered
    LaunchedEffect(showPriceError) {
        if (showPriceError) {
            Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show()
            showPriceError = false // Reset flag after showing toast
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (book == null) "Add Book" else "Edit Book") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Book Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        label = { Text("Category") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { expanded = true },
                        readOnly = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BookCategory.values().forEach { categoryItem ->
                            DropdownMenuItem(
                                text = { Text(categoryItem.displayName) },
                                onClick = {
                                    selectedCategory = categoryItem.displayName
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Show Custom Input Field if "Other" is Selected
                if (selectedCategory == BookCategory.OTHER.displayName) {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Enter Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { input ->
                        val sanitizedInput = input.trimStart { it == '0' } // Remove leading zeros
                        if (sanitizedInput.all { it.isDigit() }) {
                            quantity = if (sanitizedInput.isEmpty()) "0" else sanitizedInput
                        } else {
                            showQuantityError = true // Show error for invalid input
                        }
                    },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { input ->
                        val cleanInput = input.replace(",", "").replace(".", "") // Remove any existing commas and decimal points
                        if (cleanInput.all { it.isDigit() }) { // Ensure only numbers are entered
                            val numericValue = cleanInput.toLongOrNull() ?: 0L
                            price = if (numericValue == 0L) {
                                "0.00"
                            } else {
                                decimalFormat.format(numericValue / 100.0) // Format with commas and as a proper decimal
                            }
                            showPriceError = false // Hide error if input is valid
                        } else if (input.isEmpty()) {
                            price = "0.00" // Reset if empty
                            showPriceError = false // Hide error if input is empty
                        } else {
                            showPriceError = true // Show error if invalid input
                        }
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showPriceError
                )

                Button(
                    onClick = {
                        val finalCategory = if (selectedCategory == BookCategory.OTHER.displayName) {
                            customCategory // Use user-entered category
                        } else {
                            selectedCategory
                        }

                        val newBook = Book(
                            id = book?.id ?: 0,
                            name = name,
                            category = finalCategory,
                            quantity = quantity.toInt(),
                            price = price.replace(",", "").toDouble()
                        )
                        onSave(newBook)
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowColor
                    )
                ) {
                    Text("Save")
                }
            }
        }
    )
}

