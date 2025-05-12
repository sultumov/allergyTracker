package com.example.allergytracker.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Утилитарный класс для работы с датами
 */
object DateUtils {
    private val fullDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
    private val dateOnlyFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    
    /**
     * Форматирует дату в читабельный вид
     */
    fun formatDate(date: Date): String {
        return fullDateFormat.format(date)
    }
    
    /**
     * Форматирует дату без времени
     */
    fun formatDateOnly(date: Date): String {
        return dateOnlyFormat.format(date)
    }
    
    /**
     * Возвращает относительное время (сегодня, вчера, etc.)
     */
    fun getRelativeTimeSpan(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        
        return when {
            diffInDays == 0L -> "Сегодня"
            diffInDays == 1L -> "Вчера"
            diffInDays < 7L -> "$diffInDays дн. назад"
            diffInDays < 30L -> "${diffInDays / 7} нед. назад"
            else -> formatDateOnly(date)
        }
    }
    
    /**
     * Проверяет, является ли дата сегодняшней
     */
    fun isToday(date: Date): Boolean {
        val today = Date()
        return formatDateOnly(today) == formatDateOnly(date)
    }
    
    /**
     * Проверяет, входит ли дата в указанный период (в днях)
     */
    fun isWithinPeriod(date: Date, daysAgo: Int): Boolean {
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        
        return diffInDays <= daysAgo
    }
} 