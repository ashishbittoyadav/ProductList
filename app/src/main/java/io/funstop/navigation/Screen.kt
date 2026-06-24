package io.funstop.navigation
sealed class Screen(val route: String) {

    object ProductList : Screen("product_list")

    object ProductDetail : Screen("product_detail/{productId}") {

        fun createRoute(productId: Int): String {
            return "product_detail/$productId"
        }
    }
}