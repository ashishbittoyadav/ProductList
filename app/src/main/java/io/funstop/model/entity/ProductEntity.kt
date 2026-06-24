package io.funstop.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.funstop.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,

    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    val stock: Int,
    // Store complex fields as JSON/String
    val images: String,
    val tags: String,

    // for countdown timer
    val flashEndTime: Long
)