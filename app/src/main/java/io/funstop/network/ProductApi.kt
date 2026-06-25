package io.funstop.network

import io.funstop.model.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int=10
    ): ProductResponse

}