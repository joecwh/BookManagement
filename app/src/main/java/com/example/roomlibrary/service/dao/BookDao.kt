package com.example.roomlibrary.service.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.roomlibrary.service.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM book ORDER BY id DESC")
    fun getAllItems(): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE category = :category ORDER BY id DESC")
    fun getBooksByCategory(category: String): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE id = :id")
    suspend fun findById(id: Int): Book?

    @Query("SELECT DISTINCT category FROM book")
    fun getAllCategories(): Flow<List<String>>
}