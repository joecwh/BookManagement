package com.example.roomlibrary.service.viewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomlibrary.service.dao.BookDao
import com.example.roomlibrary.service.database.BookDatabase
import com.example.roomlibrary.service.entity.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BookViewModel(context: Context) : ViewModel() {
    private val dao: BookDao = BookDatabase.getInstance(context).bookDao()

    fun saveBook(book: Book) {
        viewModelScope.launch {
            if (book.id == 0) {
                dao.insert(book)
            } else {
                dao.update(book)
            }
        }
    }

    fun findBookById(id: Int, onResult: (Book?) -> Unit) {
        viewModelScope.launch {
            val book = dao.findById(id)
            onResult(book)
        }
    }

    fun getAllBooks() = dao.getAllItems()

    fun getBooksByCategory(category: String): Flow<List<Book>> = dao.getBooksByCategory(category)

    fun getAllCategories() = dao.getAllCategories()

    fun delete(book: Book) {
        viewModelScope.launch {
            dao.delete(book)
        }
    }
}