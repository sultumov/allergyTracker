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
import com.example.allergytracker.databinding.FragmentAddAllergyBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.common.BaseFormFragment
import com.example.allergytracker.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class AddAllergyFragment : BaseFormFragment() {

    private var _binding: FragmentAddAllergyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getToolbar(): View? = binding.toolbar

    override fun setupFormControls() {
        setupSpinners()
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
                viewModel.operationState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> showLoading(true)
                        is UiState.Success -> {
                            showLoading(false)
                            showSuccessAndNavigateBack("Аллергия успешно добавлена")
                        }
                        is UiState.Error -> {
                            showLoading(false)
                            showError("Ошибка при сохранении: ${state.message}")
                        }
                        else -> showLoading(false)
                    }
                }
            }
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

            val allergy = Allergy(
                id = UUID.randomUUID().toString(),
                name = name,
                category = category,
                severity = severity,
                description = description,
                createdAt = Date(),
                isActive = true
            )

            viewModel.addAllergy(allergy)
        } catch (e: Exception) {
            Timber.e(e, "Error saving allergy")
            showError("Ошибка при сохранении аллергии")
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