package com.example.allergytracker.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Утилитный класс для работы с датами
 */
object DateUtils {

    private val DEFAULT_DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val DATE_TIME_FORMAT = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    /**
     * Форматирует дату в строку формата "dd.MM.yyyy"
     */
    fun formatDate(date: Date): String {
        return DEFAULT_DATE_FORMAT.format(date)
    }

    /**
     * Форматирует дату и время в строку формата "dd.MM.yyyy HH:mm"
     */
    fun formatDateTime(date: Date): String {
        return DATE_TIME_FORMAT.format(date)
    }

    /**
     * Проверяет, является ли дата сегодняшней
     */
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Проверяет, находится ли дата в пределах текущей недели
     */
    fun isThisWeek(date: Date): Boolean {
        val now = Date()
        val diff = now.time - date.time
        return diff < TimeUnit.DAYS.toMillis(7)
    }

    /**
     * Проверяет, находится ли дата в пределах текущего месяца
     */
    fun isThisMonth(date: Date): Boolean {
        val now = Date()
        val diff = now.time - date.time
        return diff < TimeUnit.DAYS.toMillis(30)
    }

    /**
     * Проверяет, находится ли дата в пределах указанного количества дней от текущей
     */
    fun isWithinDays(date: Date, days: Int): Boolean {
        val now = Date()
        val diff = now.time - date.time
        return diff < TimeUnit.DAYS.toMillis(days.toLong())
    }
} 