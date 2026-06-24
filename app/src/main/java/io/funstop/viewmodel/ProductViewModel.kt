package io.funstop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.funstop.mapper.ProductMapper
import io.funstop.model.ProductResponse
import io.funstop.navigation.UiEvent
import io.funstop.repository.LocalRepository
import io.funstop.repository.WebRepository
import io.funstop.uiState.ProductUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductViewModel @Inject constructor(private val webRepository: WebRepository,private val localRepository: LocalRepository): ViewModel(){

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _timers = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timers: StateFlow<Map<Int, Long>> = _timers

    fun startTimer(productId: Int, endTimeMillis: Long) {
        viewModelScope.launch {
            countdownFlow(endTimeMillis).collect { time ->
                _timers.update { current ->
                    current + (productId to time)
                }
            }
        }
    }

    init {
        getProducts()
    }

    fun getProducts(){
        viewModelScope.launch {
            localRepository.getProducts()
                .collect {
                    if(it.isNotEmpty()){
                        _uiState.emit(ProductUiState.Success(it))

                        it.forEach { product ->
                            ProductMapper.toEntity(product).let {
                                startTimer(it.id, it.flashEndTime)
                            }
                        }

                    }else{
                        webRepository.getProducts()
                            .enqueue(object : Callback<ProductResponse> {
                                override fun onResponse(
                                    call: Call<ProductResponse?>,
                                    response: Response<ProductResponse?>
                                ) {
                                    if(response.isSuccessful){
                                        response.body()?.let {
                                            viewModelScope.launch {
                                                localRepository.insertProducts(it.products)
//                                                _uiState.emit(ProductUiState.Success(it.products))
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<ProductResponse?>, t: Throwable) {
                                    viewModelScope.launch {
                                        _uiState.emit(ProductUiState.Error(t.stackTraceToString()))
                                    }
                                }
                            })
                    }
                }
        }
    }

    fun selectProduct(productId: Int){
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigationToDetail(productId))
        }
    }

    fun countdownFlow(endTimeMillis: Long): Flow<Long> = flow {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val remaining = endTimeMillis - currentTime

            if (remaining <= 0) {
                emit(0L)
                break
            } else {
                emit(remaining)
            }
            delay(1000.milliseconds)
        }
    }
}