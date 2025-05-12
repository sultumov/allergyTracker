package com.example.allergytracker.ui.allergy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.usecase.allergy.AddAllergyUseCase
import com.example.allergytracker.domain.usecase.allergy.DeleteAllergyUseCase
import com.example.allergytracker.domain.usecase.allergy.GetAllergyByIdUseCase
import com.example.allergytracker.domain.usecase.allergy.GetAllergiesUseCase
import com.example.allergytracker.domain.usecase.allergy.UpdateAllergyUseCase
import com.example.allergytracker.ui.common.BaseViewModel
import com.example.allergytracker.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllergyViewModel @Inject constructor(
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val getAllergyByIdUseCase: GetAllergyByIdUseCase,
    private val addAllergyUseCase: AddAllergyUseCase,
    private val updateAllergyUseCase: UpdateAllergyUseCase,
    private val deleteAllergyUseCase: DeleteAllergyUseCase
) : BaseViewModel() {

    // Список аллергий
    private val _allergies = MutableLiveData<UiState<List<Allergy>>>()
    val allergies: LiveData<UiState<List<Allergy>>> = _allergies

    // Детали аллергии
    private val _allergyDetails = MutableLiveData<UiState<Allergy>>()
    val allergyDetails: LiveData<UiState<Allergy>> = _allergyDetails

    // Состояние сохранения/обновления
    private val _saveState = MutableLiveData<UiState<Boolean>>()
    val saveState: LiveData<UiState<Boolean>> = _saveState

    // Текущий тип фильтра
    private var currentFilterType = FilterType.ALL

    init {
        loadAllergies()
    }

    fun loadAllergies() {
        _allergies.postValue(UiState.Loading())
        launchSafe(viewModelScope, onError = { error ->
            _allergies.postValue(UiState.Error("Произошла ошибка: ${error.localizedMessage}"))
        }) {
            try {
                getAllergiesUseCase().collectLatest { allergies ->
                    updateAllergiesList(allergies)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading allergies")
                _allergies.postValue(UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}"))
            }
        }
    }

    fun getAllergyById(id: String) {
        _allergyDetails.postValue(UiState.Loading())
        launchSafe(viewModelScope, onError = { error ->
            _allergyDetails.postValue(UiState.Error("Произошла ошибка: ${error.localizedMessage}"))
        }) {
            try {
                getAllergyByIdUseCase(id).collectLatest { allergy ->
                    if (allergy != null) {
                        _allergyDetails.postValue(UiState.Success(allergy))
                    } else {
                        _allergyDetails.postValue(UiState.Error("Аллергия не найдена"))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading allergy details")
                _allergyDetails.postValue(UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}"))
            }
        }
    }

    fun addAllergy(allergy: Allergy) {
        _saveState.postValue(UiState.Loading())
        launchSafe(viewModelScope, onError = { error ->
            _saveState.postValue(UiState.Error("Произошла ошибка: ${error.localizedMessage}"))
        }) {
            try {
                withContext(Dispatchers.IO) {
                    addAllergyUseCase(allergy)
                }
                _saveState.postValue(UiState.Success(true))
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error adding allergy")
                _saveState.postValue(UiState.Error("Ошибка сохранения: ${e.localizedMessage}"))
            }
        }
    }

    fun updateAllergy(allergy: Allergy) {
        _saveState.postValue(UiState.Loading())
        launchSafe(viewModelScope, onError = { error ->
            _saveState.postValue(UiState.Error("Произошла ошибка: ${error.localizedMessage}"))
        }) {
            try {
                withContext(Dispatchers.IO) {
                    updateAllergyUseCase(allergy)
                }
                _saveState.postValue(UiState.Success(true))
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error updating allergy")
                _saveState.postValue(UiState.Error("Ошибка обновления: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteAllergy(allergy: Allergy) {
        launchSafe(viewModelScope, onError = { error ->
            _allergies.postValue(UiState.Error("Произошла ошибка: ${error.localizedMessage}"))
        }) {
            try {
                withContext(Dispatchers.IO) {
                    deleteAllergyUseCase(allergy.id)
                }
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting allergy")
                _allergies.postValue(UiState.Error("Ошибка удаления: ${e.localizedMessage}"))
            }
        }
    }

    fun setFilterType(filterType: FilterType) {
        if (currentFilterType != filterType) {
            currentFilterType = filterType
            
            val currentState = _allergies.value
            if (currentState is UiState.Success) {
                updateAllergiesList(currentState.data)
            }
        }
    }

    private fun updateAllergiesList(allergies: List<Allergy>) {
        val filteredList = when (currentFilterType) {
            FilterType.ALL -> allergies
            FilterType.ACTIVE -> allergies.filter { it.isActive }
            FilterType.SEVERE -> allergies.filter { 
                it.severity == Allergy.Severity.HIGH.toString() || 
                it.severity.equals("Высокая", ignoreCase = true)
            }
        }
        
        _allergies.postValue(UiState.Success(filteredList))
    }

    enum class FilterType {
        ALL, ACTIVE, SEVERE
    }
} 