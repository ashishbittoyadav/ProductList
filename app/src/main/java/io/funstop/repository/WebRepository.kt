package io.funstop.repository

import io.funstop.network.ProductApi
import javax.inject.Inject


class WebRepository @Inject constructor(val productApi: ProductApi) {

    suspend fun getProducts(page: Int) = productApi.getProducts(page)
}