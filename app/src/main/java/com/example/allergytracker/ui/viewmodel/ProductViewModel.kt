package com.example.allergytracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: Application
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getProductByBarcode(barcode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _product.value = productRepository.getProductByBarcode(barcode)
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при получении данных"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                productRepository.saveProduct(product)
                _product.value = product
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при сохранении данных"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}