package com.example.allergytracker.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Базовый класс ViewModel с общей функциональностью
 */
abstract class BaseViewModel : ViewModel() {

    protected val _operationState = MutableStateFlow<UiState<Boolean>>(UiState.Initial())
    val operationState: StateFlow<UiState<Boolean>> = _operationState

    /**
     * Выполняет операцию с корутиной и обрабатывает состояния и ошибки
     */
    protected fun launchOperation(
        showLoading: Boolean = true,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) {
                    _operationState.value = UiState.Loading()
                }
                
                operation()
                
                _operationState.value = UiState.Success(true)
                onSuccess?.invoke()
            } catch (e: Exception) {
                Timber.e(e, "Operation failed")
                _operationState.value = UiState.Error(e.message ?: "Неизвестная ошибка")
                onError?.invoke(e)
            }
        }
    }

    /**
     * Загружает данные с обработкой состояний и ошибок
     */
    protected fun <T> launchDataLoad(
        stateFlow: MutableStateFlow<UiState<T>>,
        showLoading: Boolean = true,
        dataLoader: suspend () -> T?
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) {
                    stateFlow.value = UiState.Loading()
                }
                
                val result = dataLoader()
                
                stateFlow.value = if (result != null) {
                    UiState.Success(result)
                } else {
                    UiState.Error("Данные не найдены")
                }
            } catch (e: Exception) {
                Timber.e(e, "Data loading failed")
                stateFlow.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
} 