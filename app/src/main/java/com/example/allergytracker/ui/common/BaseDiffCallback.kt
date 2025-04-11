package com.example.allergytracker.ui.common

import androidx.recyclerview.widget.DiffUtil

/**
 * Базовый класс для DiffUtil.ItemCallback, используемый в адаптерах
 * 
 * @param T тип элемента для сравнения
 * @param ID тип идентификатора элемента
 */
abstract class BaseDiffCallback<T, ID> : DiffUtil.ItemCallback<T>() {
    
    /**
     * Получает идентификатор элемента для сравнения идентичности
     */
    abstract fun getItemId(item: T): ID
    
    /**
     * Проверяет, являются ли элементы одним и тем же объектом
     * По умолчанию сравнивает идентификаторы
     */
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return getItemId(oldItem) == getItemId(newItem)
    }
}

/**
 * Обобщенный DiffCallback для объектов с идентификатором
 * 
 * @param T тип элемента для сравнения, должен иметь идентификатор типа ID
 * @param ID тип идентификатора элемента
 * @param getItemIdFunction функция для получения идентификатора из элемента
 */
class GenericDiffCallback<T, ID>(
    private val getItemIdFunction: (T) -> ID,
    private val areContentsTheSameFunction: (T, T) -> Boolean = { old, new -> old == new }
) : BaseDiffCallback<T, ID>() {
    
    override fun getItemId(item: T): ID = getItemIdFunction(item)
    
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areContentsTheSameFunction(oldItem, newItem)
    }
} 