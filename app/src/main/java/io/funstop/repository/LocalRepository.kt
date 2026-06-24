package io.funstop.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.funstop.dao.ProductDao
import io.funstop.mapper.ProductMapper
import io.funstop.model.Product
import io.funstop.model.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepository @Inject constructor(private val dao: ProductDao) {


    fun getPagingSource(): PagingSource<Int, ProductEntity> {
        return dao.getPagingSource()
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