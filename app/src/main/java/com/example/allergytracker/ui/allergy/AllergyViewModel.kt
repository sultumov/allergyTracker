package com.example.allergytracker.ui.allergy

import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.usecase.allergy.*
import com.example.allergytracker.ui.common.BaseViewModel
import com.example.allergytracker.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllergyViewModel @Inject constructor(
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val getActiveAllergiesUseCase: GetActiveAllergiesUseCase,
    private val getAllergyByIdUseCase: GetAllergyByIdUseCase,
    private val addAllergyUseCase: AddAllergyUseCase,
    private val updateAllergyUseCase: UpdateAllergyUseCase,
    private val deleteAllergyUseCase: DeleteAllergyUseCase
) : BaseViewModel() {

    private val _allergiesState = MutableStateFlow<UiState<List<Allergy>>>(UiState.Loading())
    val allergiesState: StateFlow<UiState<List<Allergy>>> = _allergiesState

    private val _allergyState = MutableStateFlow<UiState<Allergy?>>(UiState.Loading())
    val allergyState: StateFlow<UiState<Allergy?>> = _allergyState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showOnlyActive = MutableStateFlow(true)
    val showOnlyActive: StateFlow<Boolean> = _showOnlyActive

    init {
        loadAllergies()
    }

    fun loadAllergies() {
        viewModelScope.launch {
            _allergiesState.value = UiState.Loading()
            try {
                val allergiesFlow = if (_showOnlyActive.value) {
                    getActiveAllergiesUseCase()
                } else {
                    getAllergiesUseCase()
                }

                allergiesFlow
                    .catch { e ->
                        Timber.e(e, "Error loading allergies")
                        _allergiesState.value = UiState.Error(e.message ?: "Unknown error")
                    }
                    .map { allergies ->
                        applySearchFilter(allergies)
                    }
                    .collect { filteredAllergies ->
                        _allergiesState.value = UiState.Success(filteredAllergies)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error setting up allergies flow")
                _allergiesState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadAllergyById(id: String) {
        launchDataLoad(_allergyState) {
            getAllergyByIdUseCase(id).first()
        }
    }

    fun addAllergy(allergy: Allergy) {
        launchOperation(
            operation = { addAllergyUseCase(allergy) },
            onSuccess = { loadAllergies() }
        )
    }

    fun updateAllergy(allergy: Allergy) {
        launchOperation(
            operation = { updateAllergyUseCase(allergy) },
            onSuccess = {
                loadAllergies()
                loadAllergyById(allergy.id) // Refresh the current allergy view if needed
            }
        )
    }

    fun deleteAllergy(id: String) {
        launchOperation(
            operation = { deleteAllergyUseCase(id) },
            onSuccess = { loadAllergies() }
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadAllergies()
    }

    fun setShowOnlyActive(showActive: Boolean) {
        _showOnlyActive.value = showActive
        loadAllergies()
    }

    private fun applySearchFilter(allergies: List<Allergy>): List<Allergy> {
        val query = _searchQuery.value.trim().lowercase()
        return if (query.isEmpty()) {
            allergies
        } else {
            allergies.filter { allergy ->
                allergy.name.lowercase().contains(query) ||
                        allergy.category.lowercase().contains(query) ||
                        allergy.description.lowercase().contains(query)
            }
        }
    }
} 
} 