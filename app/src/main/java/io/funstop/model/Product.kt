package io.funstop.model

import androidx.room.Ignore

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val brand: String?,
    val category: String?,
    val thumbnail: String,
    val stock: Int,
    val images: List<String>,
    val tags: List<String>,
    val flashEndTime: Long,

    // 👇 FIX: make these nullable OR REMOVE
    @Ignore val availabilityStatus: String? = null,
    @Ignore val dimensions: Dimensions? = null,
    @Ignore val discountPercentage: Double? = null,
    @Ignore val meta: Meta? = null,
    @Ignore val minimumOrderQuantity: Int? = null,
    @Ignore val returnPolicy: String? = null,
    @Ignore val reviews: List<Review>? = null,
    @Ignore val shippingInformation: String? = null,
    @Ignore val sku: String? = null,
    @Ignore val warrantyInformation: String? = null,
    @Ignore val weight: Double? = null
)