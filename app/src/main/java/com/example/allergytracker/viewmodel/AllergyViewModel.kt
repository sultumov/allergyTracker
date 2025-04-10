package com.example.allergytracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.AppDatabase
import com.example.allergytracker.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AllergyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _recordsState = MutableStateFlow<UiState<List<AllergyRecord>>>(UiState.Loading)
    val recordsState: StateFlow<UiState<List<AllergyRecord>>> = _recordsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    private var allRecords: List<AllergyRecord> = emptyList()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            try {
                _recordsState.value = UiState.Loading
                allRecords = database.allergyDao().getAllRecords()
                applyFilters()
            } catch (e: Exception) {
                Log.e("AllergyViewModel", "Error loading records", e)
                _recordsState.value = UiState.Error("Failed to load records: ${e.message}")
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun setFilterType(type: FilterType) {
        _filterType.value = type
        applyFilters()
    }

    private fun applyFilters() {
        val filteredByDate = when (_filterType.value) {
            FilterType.ALL -> allRecords
            FilterType.TODAY -> allRecords.filter { isToday(it.date) }
            FilterType.WEEK -> allRecords.filter { isThisWeek(it.date) }
            FilterType.MONTH -> allRecords.filter { isThisMonth(it.date) }
        }

        val searchQuery = _searchQuery.value.lowercase()
        val filteredBySearch = if (searchQuery.isNotEmpty()) {
            filteredByDate.filter {
                it.symptoms.lowercase().contains(searchQuery) ||
                it.triggers.lowercase().contains(searchQuery) ||
                (it.medication?.lowercase()?.contains(searchQuery) == true)
            }
        } else {
            filteredByDate
        }

        _recordsState.value = if (filteredBySearch.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(filteredBySearch)
        }
    }

    fun addRecord(record: AllergyRecord) {
        viewModelScope.launch {
            try {
                database.allergyDao().insertRecord(record)
                loadRecords()
            } catch (e: Exception) {
                Log.e("AllergyViewModel", "Error adding record", e)
                _recordsState.value = UiState.Error("Failed to add record: ${e.message}")
            }
        }
    }

    fun deleteRecord(record: AllergyRecord) {
        viewModelScope.launch {
            try {
                database.allergyDao().deleteRecord(record)
                loadRecords()
            } catch (e: Exception) {
                Log.e("AllergyViewModel", "Error deleting record", e)
                _recordsState.value = UiState.Error("Failed to delete record: ${e.message}")
            }
        }
    }

    private fun isToday(dateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val recordDate = dateFormat.parse(dateString) ?: return false
            val today = Calendar.getInstance()
            val recordCalendar = Calendar.getInstance().apply { time = recordDate }
            
            today.get(Calendar.YEAR) == recordCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == recordCalendar.get(Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            Log.e("AllergyViewModel", "Error parsing date: $dateString", e)
            false
        }
    }

    private fun isThisWeek(dateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val recordDate = dateFormat.parse(dateString) ?: return false
            val today = Calendar.getInstance()
            val recordCalendar = Calendar.getInstance().apply { time = recordDate }
            
            today.get(Calendar.YEAR) == recordCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.WEEK_OF_YEAR) == recordCalendar.get(Calendar.WEEK_OF_YEAR)
        } catch (e: Exception) {
            Log.e("AllergyViewModel", "Error parsing date: $dateString", e)
            false
        }
    }

    private fun isThisMonth(dateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val recordDate = dateFormat.parse(dateString) ?: return false
            val today = Calendar.getInstance()
            val recordCalendar = Calendar.getInstance().apply { time = recordDate }
            
            today.get(Calendar.YEAR) == recordCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == recordCalendar.get(Calendar.MONTH)
        } catch (e: Exception) {
            Log.e("AllergyViewModel", "Error parsing date: $dateString", e)
            false
        }
    }

    enum class FilterType {
        ALL, TODAY, WEEK, MONTH
    }
}