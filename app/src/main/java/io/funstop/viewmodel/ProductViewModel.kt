package io.funstop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.funstop.model.ProductResponse
import io.funstop.navigation.UiEvent
import io.funstop.repository.LocalRepository
import io.funstop.repository.WebRepository
import io.funstop.uiState.ProductUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val webRepository: WebRepository,private val localRepository: LocalRepository): ViewModel(){

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getProducts()
    }

    fun getProducts(){
        viewModelScope.launch {
            localRepository.getProducts()
                .collect {
                    if(it.isNotEmpty()){
                        _uiState.emit(ProductUiState.Success(it))
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
                                                _uiState.emit(ProductUiState.Success(it.products))
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

}