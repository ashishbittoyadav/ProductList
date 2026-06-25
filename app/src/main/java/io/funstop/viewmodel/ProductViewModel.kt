package io.funstop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.funstop.model.Product
import io.funstop.model.ProductResponse
import io.funstop.model.entity.ProductEntity
import io.funstop.navigation.UiEvent
import io.funstop.repository.EventRepository
import io.funstop.repository.LocalRepository
import io.funstop.repository.WebRepository
import io.funstop.uiState.ProductUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val webRepository: WebRepository,
    private val localRepository: LocalRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    // ---------------- UI STATE ----------------
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    // ---------------- PAGING ----------------
    val pagedProducts: Flow<PagingData<ProductEntity>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                localRepository.getPagingSource()
            }
        ).flow.cachedIn(viewModelScope)

    // ---------------- MANUAL PAGINATION ----------------
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) return

        isLoading = true

        viewModelScope.launch {
            try {
                // show loading only for first page
                if (currentPage == 1) {
                    _uiState.emit(ProductUiState.Loading)
                }

                val response = webRepository.getProducts(currentPage)
                val products = response.products

                if (products.isEmpty()) {
                    isLastPage = true

                    if (currentPage == 1) {
                        _uiState.emit(ProductUiState.Error("no item found"))
                    }
                } else {
                    localRepository.insertProducts(products)
                    currentPage++

                    _uiState.emit(ProductUiState.Success(products))
                }

            } catch (e: Exception) {
                if (currentPage == 1) {
                    _uiState.emit(
                        ProductUiState.Error(e.message ?: "Something went wrong")
                    )
                }
            } finally {
                isLoading = false
            }
        }
    }

    // ---------------- TIMER ----------------
    private val _timers = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timers: StateFlow<Map<Int, Long>> = _timers

    fun startTimer(productId: Int, endTimeMillis: Long) {
        if (_timers.value.containsKey(productId)) return

        viewModelScope.launch {
            countdownFlow(endTimeMillis).collect { time ->
                _timers.update { current ->
                    current + (productId to time)
                }
            }
        }
    }

    private fun countdownFlow(endTimeMillis: Long): Flow<Long> = flow {
        while (true) {
            val remaining = endTimeMillis - System.currentTimeMillis()

            if (remaining <= 0) {
                emit(0L)
                break
            } else {
                emit(remaining)
            }

            delay(1000)
        }
    }

    // ---------------- EVENTS ----------------
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun selectProduct(productId: Int) {
        logClick(productId)

        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigationToDetail(productId))
        }
    }

    private fun logClick(productId: Int) {
        viewModelScope.launch {
            eventRepository.logEvent("product_click", "id=$productId")
        }
    }
}