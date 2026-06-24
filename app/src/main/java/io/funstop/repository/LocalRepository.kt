package io.funstop.repository

import io.funstop.dao.ProductDao
import io.funstop.mapper.ProductMapper
import io.funstop.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepository @Inject constructor(private val dao: ProductDao) {

    fun getProducts(): Flow<List<Product>> {
        return dao.getProducts().map { list ->
            list.map { ProductMapper.toDomain(it) }
        }
    }

    fun getProduct(id: Int): Flow<Product?> {
        return dao.getProductById(id).map {
            it?.let { ProductMapper.toDomain(it) }
        }
    }

    suspend fun insertProducts(products: List<Product>) {
        dao.insertAll(products.map { ProductMapper.toEntity(it) })
    }
}