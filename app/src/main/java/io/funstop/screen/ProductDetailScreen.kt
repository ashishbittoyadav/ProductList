package io.funstop.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.funstop.model.Product
import io.funstop.uiState.ProductUiState
import io.funstop.utils.Utils.formatTime
import io.funstop.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    productId: Int,
    viewModel: ProductViewModel,
    onBack: () -> Unit = {}
) {

    val state by viewModel.uiState.collectAsState()

    val timers by viewModel.timers.collectAsState()

    val product = when (state) {
        is ProductUiState.Success -> {
            (state as ProductUiState.Success)
                .products
                .find { it.id == productId }
        }

        else -> null
    }

    when {
        state is ProductUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state is ProductUiState.Error -> {
            val message = (state as ProductUiState.Error).message

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message)
            }
        }

        product == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
        }

        else -> {
            ProductDetailContent(
                innerPadding = innerPadding,
                product = product,
                timers = timers,
                onBack = onBack
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    innerPadding: PaddingValues,
    product: Product,
    timers: Map<Int, Long>,
    onBack: () -> Unit
) {

    val remaining = timers[product.id] ?: 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(innerPadding)
            .padding(horizontal = 20.dp)
    ) {

        AsyncImage(
            modifier = Modifier.size(400.dp),
            model = product.images[0],
            contentDescription = product.title
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.brand?:"",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "₹${product.price}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Rating: ${product.rating}")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Stock: ${product.stock}")

        if (remaining > 0)
            Row {
                Text("Flash Sale Ends in:")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = formatTime(remaining))
            }
    }
}