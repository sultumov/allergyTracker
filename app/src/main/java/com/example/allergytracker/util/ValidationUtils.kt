package com.example.allergytracker.util

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * Утилитный класс для валидации данных форм
 */
object ValidationUtils {
    
    /**
     * Проверяет, что строка не пустая
     */
    fun String?.isNotEmptyOrBlank(): Boolean {
        return !this.isNullOrBlank()
    }
    
    /**
     * Проверяет, что строка содержит действительный email
     */
    fun String?.isValidEmail(): Boolean {
        return !this.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    
    /**
     * Проверяет, что строка содержит только буквы
     */
    fun String?.isAlphabetic(): Boolean {
        return !this.isNullOrBlank() && Pattern.matches("[a-zA-Zа-яА-Я\\s]+", this)
    }
    
    /**
     * Проверяет, что строка содержит действительную дату в формате dd.MM.yyyy
     */
    fun String?.isValidDate(): Boolean {
        if (this.isNullOrBlank()) return false
        
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(this)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Проверяет, что значение находится в допустимом диапазоне
     */
    fun Int.isInRange(min: Int, max: Int): Boolean {
        return this in min..max
    }
    
    /**
     * Валидирует поле ввода и устанавливает ошибку, если валидация не прошла
     */
    fun validateField(
        inputLayout: TextInputLayout,
        value: String?,
        errorMessage: String,
        validationFunction: (String?) -> Boolean
    ): Boolean {
        val isValid = validationFunction(value)
        
        inputLayout.error = if (isValid) null else errorMessage
        
        return isValid
    }
    
    /**
     * Валидирует обязательное поле (не должно быть пустым)
     */
    fun validateRequiredField(inputLayout: TextInputLayout, value: String?, errorMessage: String = "Поле обязательно"): Boolean {
        return validateField(inputLayout, value, errorMessage) { it.isNotEmptyOrBlank() }
    }
    
    /**
     * Валидирует поле email
     */
    fun validateEmailField(inputLayout: TextInputLayout, value: String?, errorMessage: String = "Неверный формат email"): Boolean {
        return validateField(inputLayout, value, errorMessage) { it.isValidEmail() }
    }
    
    /**
     * Валидирует поле даты
     */
    fun validateDateField(inputLayout: TextInputLayout, value: String?, errorMessage: String = "Неверный формат даты"): Boolean {
        return validateField(inputLayout, value, errorMessage) { it.isValidDate() }
    }
    
    /**
     * Парсит строку даты в объект Date
     */
    fun parseDate(dateString: String, format: String = "dd.MM.yyyy"): Date? {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
} 