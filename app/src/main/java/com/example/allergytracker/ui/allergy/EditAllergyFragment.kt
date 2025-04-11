package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.databinding.FragmentEditAllergyBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.common.BaseFormFragment
import com.example.allergytracker.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class EditAllergyFragment : BaseFormFragment() {

    private var _binding: FragmentEditAllergyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergyViewModel by viewModels()
    private val args: EditAllergyFragmentArgs by navArgs()

    private var currentAllergy: Allergy? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getToolbar(): View? = binding.toolbar

    override fun setupFormControls() {
        setupSpinners()
        
        // Загружаем данные аллергии
        viewModel.loadAllergyById(args.allergyId)
    }

    private fun setupSpinners() {
        // Настройка выпадающего списка для категорий
        val categories = listOf(
            "Пищевая",
            "Лекарственная",
            "Бытовая",
            "Пыльцевая",
            "Эпидермальная",
            "Инсектная",
            "Другая"
        )
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.autoCompleteCategory.setAdapter(categoryAdapter)

        // Настройка выпадающего списка для тяжести
        val severities = listOf(
            "Низкая",
            "Средняя",
            "Высокая"
        )
        val severityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            severities
        )
        binding.autoCompleteSeverity.setAdapter(severityAdapter)
    }

    override fun setupButtons() {
        binding.buttonSave.setOnClickListener {
            if (validateInputs()) {
                saveData()
            }
        }
    }
    
    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allergyState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> showLoading(true)
                        is UiState.Success -> {
                            showLoading(false)
                            state.data?.let { populateFields(it) }
                        }
                        is UiState.Error -> {
                            showLoading(false)
                            showError("Ошибка загрузки данных: ${state.message}")
                        }
                    }
                }
            }
        }
    }
    
    private fun populateFields(allergy: Allergy) {
        currentAllergy = allergy
        
        binding.apply {
            editTextName.setText(allergy.name)
            autoCompleteCategory.setText(allergy.category)
            autoCompleteSeverity.setText(allergy.severity)
            editTextDescription.setText(allergy.description)
            
            // Также обновляем статус активности
            switchActiveStatus.isChecked = allergy.isActive
        }
    }

    override fun validateInputs(): Boolean {
        var isValid = true

        // Проверка имени
        if (binding.editTextName.text.isNullOrBlank()) {
            binding.textInputLayoutName.error = "Введите название аллергии"
            isValid = false
        } else {
            binding.textInputLayoutName.error = null
        }

        // Проверка категории
        if (binding.autoCompleteCategory.text.isNullOrBlank()) {
            binding.textInputLayoutCategory.error = "Выберите категорию"
            isValid = false
        } else {
            binding.textInputLayoutCategory.error = null
        }

        // Проверка тяжести
        if (binding.autoCompleteSeverity.text.isNullOrBlank()) {
            binding.textInputLayoutSeverity.error = "Выберите тяжесть"
            isValid = false
        } else {
            binding.textInputLayoutSeverity.error = null
        }

        return isValid
    }

    override fun saveData() {
        try {
            val name = binding.editTextName.text.toString().trim()
            val category = binding.autoCompleteCategory.text.toString().trim()
            val severity = binding.autoCompleteSeverity.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val isActive = binding.switchActiveStatus.isChecked
            
            currentAllergy?.let { currentAllergy ->
                val updatedAllergy = currentAllergy.copy(
                    name = name,
                    category = category,
                    severity = severity,
                    description = description,
                    isActive = isActive
                )
                
                viewModel.updateAllergy(updatedAllergy)
                
                // Показываем сообщение об успехе и возвращаемся назад
                showSuccessAndNavigateBack("Аллергия успешно обновлена")
            } ?: run {
                showError("Не удалось обновить аллергию: данные не загружены")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating allergy")
            showError("Ошибка при обновлении аллергии")
        }
    }
    
    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 