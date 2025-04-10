package com.example.allergytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.model.Product
import com.example.allergytracker.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Typography.dagger

@HiltViewModel
class ProductScannerViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _scannedProduct = MutableStateFlow<Product?>(null)
    val scannedProduct: StateFlow<Product?> = _scannedProduct.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun scanProduct(barcode: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val product = productRepository.getProductByBarcode(barcode)
                _scannedProduct.value = product
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to scan product"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.addProduct(product)
                _scannedProduct.value = product
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearScannedProduct() {
        _scannedProduct.value = null
    }
} 