package io.funstop.uiState

import io.funstop.model.Product

sealed class ProductUiState {

    object Loading : ProductUiState()

    data class Success(
        val products: List<Product>
    ) : ProductUiState()

//    data class SelectedProduct(
//        val product: Product
//    ): ProductUiState()

    data class Error(
        val message: String
    ) : ProductUiState()
}