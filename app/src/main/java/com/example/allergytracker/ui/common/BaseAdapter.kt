package com.example.allergytracker.ui.common

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Базовый адаптер для RecyclerView с поддержкой DiffUtil
 */
abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    private var onItemClickListener: ((T) -> Unit)? = null
    private var onItemLongClickListener: ((T) -> Boolean)? = null

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * Устанавливает слушатель клика по элементу
     */
    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    /**
     * Устанавливает слушатель долгого клика по элементу
     */
    fun setOnItemLongClickListener(listener: (T) -> Boolean) {
        onItemLongClickListener = listener
    }

    /**
     * Возвращает элемент по позиции
     */
    fun getItemAtPosition(position: Int): T? {
        return if (position in 0 until itemCount) {
            getItem(position)
        } else {
            null
        }
    }

    /**
     * Базовый класс ViewHolder с привязкой данных
     */
    abstract class BaseViewHolder<T>(
        itemView: View,
        private val onItemClick: ((T) -> Unit)?,
        private val onItemLongClick: ((T) -> Boolean)?
    ) : RecyclerView.ViewHolder(itemView) {

        private var currentItem: T? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let { item ->
                    onItemClick?.invoke(item)
                }
            }

            itemView.setOnLongClickListener {
                currentItem?.let { item ->
                    onItemLongClick?.invoke(item) ?: false
                } ?: false
            }
        }

        /**
         * Привязывает данные к ViewHolder
         */
        fun bind(item: T) {
            currentItem = item
            bindItem(item)
        }

        /**
         * Метод для привязки данных в наследниках
         */
        abstract fun bindItem(item: T)
    }
} 