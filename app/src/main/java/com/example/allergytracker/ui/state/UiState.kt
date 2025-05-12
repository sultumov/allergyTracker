package com.example.allergytracker.ui.state

/**
 * Класс состояний UI для унификации работы с данными
 */
sealed class UiState<out T> {
    /**
     * Состояние загрузки
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Состояние успеха с данными
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Состояние ошибки с сообщением
     */
    data class Error(val message: String) : UiState<Nothing>()
} 