package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garage_items")
data class GarageItem(
    @PrimaryKey val id: Int, // Match the Car Catalog ID directly (each car can be in the garage once)
    val brand: String,
    val model: String,
    val year: Int,
    val condition: String,
    val price: String,
    val numericPrice: Double,
    val imageUrl: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    val fullName: String get() = "$year $brand $model"
}
