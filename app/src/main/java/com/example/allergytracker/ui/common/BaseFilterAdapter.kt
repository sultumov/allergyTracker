package com.example.allergytracker.ui.common

import android.view.View
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import timber.log.Timber

/**
 * Базовый адаптер для RecyclerView с поддержкой фильтрации
 */
abstract class BaseFilterAdapter<T : Any, VH : BaseAdapter.BaseViewHolder<T>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : BaseAdapter<T, VH>(diffCallback), Filterable {

    private var originalList: List<T> = emptyList()
    private var filteredList: List<T> = emptyList()
    private var lastFilter: String = ""
    
    /**
     * Устанавливает список элементов и сохраняет его для фильтрации
     */
    override fun submitList(list: List<T>) {
        originalList = list
        if (lastFilter.isNotEmpty()) {
            // Если последний фильтр существует, применяем его к новому списку
            filter.filter(lastFilter)
        } else {
            // Иначе просто показываем весь список
            filteredList = list
            super.submitList(list)
        }
    }
    
    /**
     * Очищает фильтр и показывает весь список
     */
    fun clearFilter() {
        lastFilter = ""
        filteredList = originalList
        super.submitList(originalList)
    }
    
    /**
     * Функция для фильтрации элементов
     */
    abstract fun filterItem(item: T, constraint: String): Boolean
    
    /**
     * Реализация Filter для фильтрации элементов
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterString = constraint?.toString()?.trim()?.lowercase() ?: ""
                lastFilter = filterString
                
                val results = FilterResults()
                if (filterString.isEmpty()) {
                    results.values = originalList
                    results.count = originalList.size
                } else {
                    try {
                        val filteredItems = originalList.filter { item ->
                            filterItem(item, filterString)
                        }
                        results.values = filteredItems
                        results.count = filteredItems.size
                    } catch (e: Exception) {
                        Timber.e(e, "Error filtering items")
                        results.values = originalList
                        results.count = originalList.size
                    }
                }
                
                return results
            }
            
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    filteredList = results?.values as? List<T> ?: emptyList()
                    super@BaseFilterAdapter.submitList(filteredList)
                } catch (e: Exception) {
                    Timber.e(e, "Error publishing filter results")
                    filteredList = originalList
                    super@BaseFilterAdapter.submitList(filteredList)
                }
            }
        }
    }
    
    /**
     * Показывает сообщение о пустом списке, если список пуст после фильтрации
     */
    fun showEmptyView(emptyView: View?, recyclerView: View?) {
        emptyView?.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        recyclerView?.visibility = if (filteredList.isEmpty()) View.GONE else View.VISIBLE
    }
} 