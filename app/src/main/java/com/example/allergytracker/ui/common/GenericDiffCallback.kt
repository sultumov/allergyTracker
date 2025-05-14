package com.example.allergytracker.ui.common

import androidx.recyclerview.widget.DiffUtil

/**
 * Универсальная реализация DiffUtil.ItemCallback для адаптеров RecyclerView
 * @param getItemIdFunction Функция для получения уникального идентификатора элемента
 * @param areContentsTheSameFunction Функция для проверки изменения содержимого элементов
 */
class GenericDiffCallback<T : Any>(
    private val getItemIdFunction: (T) -> Any,
    private val areContentsTheSameFunction: (T, T) -> Boolean = { old, new -> old == new }
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return getItemIdFunction(oldItem) == getItemIdFunction(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areContentsTheSameFunction(oldItem, newItem)
    }
} 