package com.example.roomlibrary
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roomlibrary.component.Loading
import com.example.roomlibrary.screen.AddEditBookScreen
import com.example.roomlibrary.screen.BookDetailScreen
import com.example.roomlibrary.screen.MainScreen
import com.example.roomlibrary.service.entity.Book
import com.example.roomlibrary.service.viewModel.BookViewModel
import com.example.roomlibrary.ui.theme.RoomLibraryTheme

// Navigation.kt
sealed class ScreenNav(val route: String) {
    object Home : ScreenNav("home")
    object BookDetail : ScreenNav("bookDetail/{bookId}") {
        fun createRoute(bookId: Int) = "bookDetail/$bookId"
    }
    object AddBook : ScreenNav("addBook")
    object EditBook : ScreenNav("editBook/{bookId}") {
        fun createRoute(bookId: Int) = "editBook/$bookId"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            val viewModel = BookViewModel(context)

            RoomLibraryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenNav.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(ScreenNav.Home.route) {
                            MainScreen(
                                navController,
                                onAddItem = {
                                    navController.navigate(ScreenNav.AddBook.route)
                                },
                                onItemClick = { book ->
                                    navController.navigate(ScreenNav.BookDetail.createRoute(book.id))
                                },
                                onDeleteItem = { book ->
                                    viewModel.delete(book)
                                }
                            )
                        }
                        composable(
                            ScreenNav.BookDetail.route,
                            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getInt("bookId")
                            if (bookId != null) {
                                var book by remember { mutableStateOf<Book?>(null) }
                                viewModel.findBookById(bookId) { foundBook ->
                                    book = foundBook
                                }
                                book?.let {
                                    BookDetailScreen (
                                        navController,
                                        it,
                                        onEdit = {
                                            navController.navigate(ScreenNav.EditBook.createRoute(bookId))
                                        }
                                    )
                                }
                            }
                        }
                        composable(
                            ScreenNav.AddBook.route
                        ) {
                            AddEditBookScreen (
                                navController,
                                book = null,
                                onSave = { myBook ->
                                    run {
                                        viewModel.saveBook(myBook)
                                    }
                                }
                            )
                        }
                        composable(
                            ScreenNav.EditBook.route,
                            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getInt("bookId")

                            if (bookId != null) {
                                var book by remember { mutableStateOf<Book?>(null) }

                                LaunchedEffect(bookId) {
                                    viewModel.findBookById(bookId) { foundBook ->
                                        book = foundBook
                                    }
                                }
                                book?.let { myBook ->
                                    AddEditBookScreen(
                                        navController = navController,
                                        book = myBook,
                                        onSave = { updatedBook ->
                                            viewModel.saveBook(updatedBook)
                                        }
                                    )
                                } ?: Loading()
                            } else {
                                Toast.makeText(context, "Invalid Book", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        }
    }
}
