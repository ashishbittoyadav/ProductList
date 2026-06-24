package io.funstop.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.funstop.navigation.Screen
import io.funstop.navigation.UiEvent
import io.funstop.ui.theme.ProductAppTheme
import io.funstop.viewmodel.ProductViewModel
import io.funstop.work_manager.EventUploadWorker

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ProductViewModel by viewModels()
            val navigationController = rememberNavController()
            LaunchedEffect(Unit) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is UiEvent.NavigationToDetail -> {
                            navigationController
                                .navigate(Screen.ProductDetail.createRoute(event.productId))
                        }

                        UiEvent.NavigationToList -> {
                            navigationController
                                .navigate(Screen.ProductList)
                        }
                    }
                }
            }
            ProductAppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimaryFixedVariant),
                    topBar = {
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.onPrimaryFixedVariant)
                                .statusBarsPadding()
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.inversePrimary
                            ),
                            text = "Product App"
                        )
                    }

                ) { innerPadding ->
                    AppNavHost(innerPadding, viewModel, navigationController)
                }
            }
        }
    }
}


@Composable
fun AppNavHost(
    innerPadding: PaddingValues,
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductList.route
    ) {

        composable(Screen.ProductList.route) {
            StartScreen(innerPadding = innerPadding, viewModel = viewModel)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(innerPadding, productId = productId, viewModel = viewModel) {
            }
        }
    }
}