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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
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
fun StartScreen(innerPadding: PaddingValues,viewModel: ProductViewModel) {

    val products = viewModel.pagedProducts.collectAsLazyPagingItems()

    val timers by viewModel.timers.collectAsState()

    LazyColumn {
        items(products.itemCount) { index ->

            val item = products[index]

            item?.let {
                ProductItem(ProductMapper.toDomain(it), timers = timers,{ viewModel.selectProduct(it.id) },viewModel)
                if (index >= products.itemCount - 2) {
                    viewModel.loadNextPage()
                }
            }
        }

        // Loader
        item {
            if (products.loadState.append is LoadState.Loading) {
                CircularProgressIndicator()
            }
        }
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
            Text(text = product.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = "₹${product.price}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W600))
            if (remaining > 0)
                Text(text = formatTime(remaining), style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red, fontWeight = FontWeight.W500))
        }
    }
}