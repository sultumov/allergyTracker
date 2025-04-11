package com.example.allergytracker.ui.common

/**
 * Общий класс для UI состояний
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()

    companion object {
        fun <T> loading(): UiState<T> = Loading
        fun <T> success(data: T): UiState<T> = Success(data)
        fun <T> error(message: String): UiState<T> = Error(message)
    }
}

/**
 * Проверка состояния и выполнение соответствующего действия
 */
inline fun <T> UiState<T>.handle(
    onLoading: () -> Unit = {},
    onSuccess: (T) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    when (this) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(message)
    }
} 