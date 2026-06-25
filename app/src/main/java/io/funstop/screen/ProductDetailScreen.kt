package io.funstop.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    val context = LocalContext.current
    val remaining = timers[product.id] ?: 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        AsyncImage(
            modifier = Modifier.size(400.dp),
            model = product.images[0],
            contentDescription = product.title
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W600)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.brand?:"",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W500)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "₹${product.price}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W800)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Rating: ", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W800))
            Text("${product.rating}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W500))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Stock: ",style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W800))
            Text("${product.stock}",style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W500))
        }

        if (remaining > 0)
            Row {
                Text("Flash Sale Ends in:",style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W800))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = formatTime(remaining),style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W500))
            }

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = {
                Toast.makeText(context, "Item added in the cart.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background, disabledContentColor = Color.Gray, disabledContainerColor = Color.White)
        ) {
            Text("Add to Cart")
        }
    }
}