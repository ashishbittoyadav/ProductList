package io.funstop.mapper

import io.funstop.model.Dimensions
import io.funstop.model.Meta
import io.funstop.model.Product
import io.funstop.model.entity.ProductEntity
import kotlin.time.Duration.Companion.hours

object ProductMapper {

    fun toEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            title = product.title,
            description = product.description,
            price = product.price,
            rating = product.rating,
            brand = product.brand,
            category = product.category,
            thumbnail = product.thumbnail,
            stock = product.stock,
            images = product.images.joinToString(","),
            tags = product.tags.joinToString(","),
            flashEndTime = System.currentTimeMillis() + product.id.hours.inWholeMilliseconds
        )
    }

    fun toDomain(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            price = entity.price,
            rating = entity.rating,
            brand = entity.brand?:"",
            category = entity.category,
            thumbnail = entity.thumbnail,
            stock = entity.stock,

            // Default / dummy values for now
            availabilityStatus = "",
            discountPercentage = 0.0,
            dimensions = Dimensions(0.0, 0.0, 0.0),
            images = if (entity.images.isEmpty()) emptyList() else entity.images.split(","),
            meta = Meta("", "", "", updatedAt = ""),
            minimumOrderQuantity = 1,
            returnPolicy = "",
            reviews = emptyList(),
            shippingInformation = "",
            sku = "",
            tags = if (entity.tags.isEmpty()) emptyList() else entity.tags.split(","),
            warrantyInformation = "",
            weight = 0
        )
    }
}