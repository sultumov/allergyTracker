package com.example.allergytracker.ui.allergy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.usecase.allergy.AddAllergyUseCase
import com.example.allergytracker.domain.usecase.allergy.DeleteAllergyUseCase
import com.example.allergytracker.domain.usecase.allergy.GetAllergyByIdUseCase
import com.example.allergytracker.domain.usecase.allergy.GetAllergiesUseCase
import com.example.allergytracker.domain.usecase.allergy.UpdateAllergyUseCase
import com.example.allergytracker.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
) : ViewModel() {

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

    // Обработчик исключений
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Error in ViewModel")
        _allergies.postValue(UiState.Error("Произошла ошибка: ${throwable.localizedMessage}"))
    }

    init {
        loadAllergies()
    }

    fun loadAllergies() {
        _allergies.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                val allergies = withContext(Dispatchers.IO) {
                    getAllergiesUseCase()
                }
                updateAllergiesList(allergies)
            } catch (e: Exception) {
                Timber.e(e, "Error loading allergies")
                _allergies.value = UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}")
            }
        }
    }

    fun getAllergyById(id: Long) {
        _allergyDetails.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                val allergy = withContext(Dispatchers.IO) {
                    getAllergyByIdUseCase(id)
                }
                
                if (allergy != null) {
                    _allergyDetails.value = UiState.Success(allergy)
                } else {
                    _allergyDetails.value = UiState.Error("Аллергия не найдена")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading allergy details")
                _allergyDetails.value = UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}")
            }
        }
    }

    fun addAllergy(allergy: Allergy) {
        _saveState.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    addAllergyUseCase(allergy)
                }
                _saveState.value = UiState.Success(true)
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error adding allergy")
                _saveState.value = UiState.Error("Ошибка сохранения: ${e.localizedMessage}")
            }
        }
    }

    fun updateAllergy(allergy: Allergy) {
        _saveState.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    updateAllergyUseCase(allergy)
                }
                _saveState.value = UiState.Success(true)
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error updating allergy")
                _saveState.value = UiState.Error("Ошибка обновления: ${e.localizedMessage}")
            }
        }
    }

    fun deleteAllergy(allergy: Allergy) {
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    deleteAllergyUseCase(allergy.id)
                }
                loadAllergies()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting allergy")
                _allergies.value = UiState.Error("Ошибка удаления: ${e.localizedMessage}")
            }
        }
    }

    fun setFilterType(filterType: FilterType) {
        if (currentFilterType != filterType) {
            currentFilterType = filterType
            
            _allergies.value?.let { state ->
                if (state is UiState.Success) {
                    updateAllergiesList(state.data)
                }
            }
        }
    }

    private fun updateAllergiesList(allergies: List<Allergy>) {
        val filteredList = when (currentFilterType) {
            FilterType.ALL -> allergies
            FilterType.ACTIVE -> allergies.filter { it.isActive }
            FilterType.SEVERE -> allergies.filter { 
                it.severity == "4" || it.severity == "5" || it.severity.equals("Высокая", ignoreCase = true)
            }
        }
        
        _allergies.value = if (filteredList.isEmpty()) {
            UiState.Success(emptyList())
        } else {
            UiState.Success(filteredList)
        }
    }

    enum class FilterType {
        ALL, ACTIVE, SEVERE
    }
} 
} 