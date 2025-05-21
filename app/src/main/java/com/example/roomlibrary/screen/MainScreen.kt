package com.example.roomlibrary.screen
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomlibrary.component.CategoryCard
import com.example.roomlibrary.component.InventoryItem
import com.example.roomlibrary.service.entity.Book
import com.example.roomlibrary.service.viewModel.BookViewModel
import com.example.roomlibrary.ui.theme.LightGrayColor
import com.example.roomlibrary.ui.theme.YellowColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    onAddItem: () -> Unit, // add book fun
    onItemClick: (Book) -> Unit, // click book fun
    onDeleteItem: (Book) -> Unit
) {
    val context = LocalContext.current
    val viewModel = BookViewModel(context)

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }  // Search Query State

    val books: Flow<List<Book>> = if (selectedCategory == "All") {
        viewModel.getAllBooks()
    } else {
        viewModel.getBooksByCategory(selectedCategory)
    }

    val categories = viewModel.getAllCategories()

    val bookList by books
        .catch { emit(emptyList()) } // if error return empty list
        .collectAsState(initial = emptyList())
    val categoryList by categories
        .catch { emit(emptyList()) }
        .collectAsState(initial = emptyList())

    val filteredBooks = bookList.filter {
        it.name.contains(searchQuery.text, ignoreCase = true)  // Filter books by name
    }

    val showBook = remember { mutableStateOf(false) }
    showBook.value = bookList.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Book Management",
                        fontWeight = FontWeight.Bold // Make title bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LightGrayColor,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItem,
                containerColor = YellowColor,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = Color.White,
                )
            }
        },
        modifier = Modifier.fillMaxHeight(),
        containerColor = LightGrayColor
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)), // Rounded corners
                    placeholder = { Text("Search by book name", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White, // White background
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Category Tags (Below Search Bar)
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .horizontalScroll(scrollState)
            ) {
                CategoryCard(
                    categoryName = "All",
                    isSelected = selectedCategory == "All",
                    onClick = { selectedCategory = "All" }
                )

                Spacer(modifier = Modifier.padding(end = 6.dp))

                for (category in categoryList) {
                    Box(
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        CategoryCard(
                            categoryName = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            // Books List (Filtered by Search)
            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No books found",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(filteredBooks) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.elevatedCardElevation(4.dp)
                        ) {
                            InventoryItem(
                                book = book,
                                onItemClick = { onItemClick(book) },
                                onDeleteItem = { onDeleteItem(book) },
                            )
                        }
                    }
                }
            }
        }
    }
}