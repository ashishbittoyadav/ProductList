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

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _timers = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timers: StateFlow<Map<Int, Long>> = _timers

    val pagedProducts: Flow<PagingData<ProductEntity>> =
        localRepository.getPagingSource()
            .let { pagingSourceFactory ->
                Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { pagingSourceFactory }
                ).flow
            }
            .cachedIn(viewModelScope)

    init {
        fetchFromApi()
    }
    private fun fetchFromApi() {
        webRepository.getProducts()
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse?>,
                    response: Response<ProductResponse?>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            viewModelScope.launch {
                                localRepository.insertProducts(data.products)
                                _uiState.emit(ProductUiState.Success(data.products))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ProductResponse?>, t: Throwable) {
                    viewModelScope.launch {
                        _uiState.emit(
                            ProductUiState.Error(t.message ?: "Something went wrong")
                        )
                    }
                }
            })
    }
    fun startTimer(productId: Int, endTimeMillis: Long) {
        // Prevent duplicate timers
        if (_timers.value.containsKey(productId)) return

        viewModelScope.launch {
            countdownFlow(endTimeMillis).collect { time ->
                _timers.update { current ->
                    current + (productId to time)
                }
            }
        }
    }

    fun countdownFlow(endTimeMillis: Long): Flow<Long> = flow {
        while (true) {
            val remaining = endTimeMillis - System.currentTimeMillis()

            if (remaining <= 0) {
                emit(0L)
                break
            } else {
                emit(remaining)
            }
            delay(1000.milliseconds)
        }
    }
    fun selectProduct(productId: Int) {
        logClick(productId)
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigationToDetail(productId))
        }
    }

    fun logClick(productId: Int){
        viewModelScope.launch {
            eventRepository.logEvent("product_click", "id=$productId")
        }
    }
}