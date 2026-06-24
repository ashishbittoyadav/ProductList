package io.funstop.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import io.funstop.mapper.ProductMapper
import io.funstop.model.Product
import io.funstop.uiState.ProductUiState
import io.funstop.utils.Utils.formatTime
import io.funstop.viewmodel.ProductViewModel
import io.funstop.work_manager.EventUploadWorker

@Composable
fun StartScreen(
    innerPadding: PaddingValues,
    viewModel: ProductViewModel,
//    onProductClick: (Int) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    val timers by viewModel.timers.collectAsState()

    when (state) {
        is ProductUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProductUiState.Success -> {

            Box(modifier = Modifier.padding(innerPadding)) {

                val products = viewModel.pagedProducts.collectAsLazyPagingItems()

                LazyColumn {
                    items(products.itemCount) { index ->
                        products[index]?.let { product ->
                            ProductItem(
                                product = ProductMapper.toDomain(product),
                                timers = timers,
                                onClick = {
                                    viewModel.selectProduct(product.id)
                                },
                                viewModel = viewModel // pass this
                            )
                        }
                    }
                }

            }
        }

        is ProductUiState.Error -> {
            val message = (state as ProductUiState.Error).message

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message)
            }
        }

        else -> {}
    }
}

@Composable
fun ProductItem(
    product: Product,
    timers: Map<Int, Long>,
    onClick: () -> Unit,
    viewModel: ProductViewModel
) {
    val remaining = timers[product.id] ?: 0L

    LaunchedEffect(product.id) {
        viewModel.startTimer(product.id, ProductMapper.toEntity(product).flashEndTime)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = product.thumbnail,
            contentDescription = product.title
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = product.title)
            Text(text = "₹${product.price}")
            if (remaining > 0)
                Text(text = formatTime(remaining))
        }
    }
}