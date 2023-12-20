package com.example.calorieuas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemName: String,
    val calories: String,
    val deskripsi: String,
    val isSynced: Boolean = false,
    val firebaseId: String? = null
)