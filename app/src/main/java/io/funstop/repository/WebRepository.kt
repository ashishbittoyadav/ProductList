package io.funstop.repository

import io.funstop.network.ProductApi
import javax.inject.Inject


class WebRepository @Inject constructor(val productApi: ProductApi) {

    fun getProducts() = productApi.getProducts()
}