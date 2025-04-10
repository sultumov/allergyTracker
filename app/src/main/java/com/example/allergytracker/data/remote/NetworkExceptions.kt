package com.example.allergytracker.data.remote

import java.io.IOException

sealed class NetworkException : IOException() {
    data class NoInternetConnection(override val message: String = "Нет подключения к интернету") : NetworkException()
    data class ServerError(override val message: String = "Ошибка сервера") : NetworkException()
    data class ProductNotFound(override val message: String = "Продукт не найден") : NetworkException()
    data class InvalidBarcode(override val message: String = "Неверный формат штрих-кода") : NetworkException()
    data class UnknownError(override val message: String = "Неизвестная ошибка") : NetworkException()
} 