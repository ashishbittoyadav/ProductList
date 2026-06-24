package io.funstop.network

import io.funstop.model.ProductResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductApi {

    @GET("/products")
    fun getProducts(): Call<ProductResponse>

}