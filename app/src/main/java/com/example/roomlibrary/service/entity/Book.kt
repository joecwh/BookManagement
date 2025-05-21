package com.example.roomlibrary.service.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class Book (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double
)