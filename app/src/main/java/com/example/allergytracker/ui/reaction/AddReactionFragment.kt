package com.example.allergytracker.ui.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.databinding.FragmentAddReactionBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.ui.allergy.AllergyViewModel
import com.example.allergytracker.ui.common.UiState
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddReactionFragment : Fragment() {

    private var _binding: FragmentAddReactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReactionViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()
    private val args: AddReactionFragmentArgs by navArgs()

    private var selectedDate: Date = Date()
    private var selectedAllergy: Allergy? = null
    private val selectedSymptoms = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupDatePicker()
        setupSeveritySpinner()
        setupSymptomChips()
        setupSaveButton()
        loadAllergies()
        
        // Если передан ID аллергии, загружаем её данные
        args.allergyId?.let { allergyId ->
            allergyViewModel.loadAllergyById(allergyId)
        }
        
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePicker() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.textInputDate.setText(dateFormat.format(selectedDate))
        
        binding.textInputDate.setOnClickListener {
            showDateTimePicker()
        }
    }
    
    private fun showDateTimePicker() {
        // Создаем диалог выбора даты
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .setSelection(selectedDate.time)
            .build()
            
        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            // После выбора даты показываем диалог выбора времени
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateInMillis
            
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Выберите время")
                .build()
                
            timePicker.addOnPositiveButtonClickListener {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)
                
                selectedDate = calendar.time
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                binding.textInputDate.setText(dateFormat.format(selectedDate))
            }
            
            timePicker.show(childFragmentManager, "TIME_PICKER")
        }
        
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun setupSeveritySpinner() {
        val severities = listOf(
            "Легкая",
            "Умеренная",
            "Тяжелая"
        )
        val severityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            severities
        )
        binding.autoCompleteSeverity.setAdapter(severityAdapter)
    }

    private fun setupSymptomChips() {
        val commonSymptoms = listOf(
            "Сыпь",
            "Зуд",
            "Отёк",
            "Насморк",
            "Чихание",
            "Кашель",
            "Затрудненное дыхание",
            "Тошнота",
            "Боль в животе",
            "Головная боль",
            "Головокружение"
        )
        
        commonSymptoms.forEach { symptom ->
            val chip = Chip(requireContext()).apply {
                text = symptom
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedSymptoms.add(symptom)
                    } else {
                        selectedSymptoms.remove(symptom)
                    }
                }
            }
            binding.chipGroupSymptoms.addView(chip)
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateInputs()) {
                saveReaction()
            }
        }
    }
    
    private fun loadAllergies() {
        allergyViewModel.loadAllergies()
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за списком аллергий
                launch {
                    allergyViewModel.allergiesState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                setupAllergySpinner(state.data)
                            }
                            is UiState.Error -> {
                                showError("Ошибка загрузки списка аллергий: ${state.message}")
                            }
                            else -> {}
                        }
                    }
                }
                
                // Наблюдаем за деталями конкретной аллергии
                launch {
                    allergyViewModel.allergyState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                state.data?.let { allergy ->
                                    selectedAllergy = allergy
                                    binding.autoCompleteAllergy.setText(allergy.name)
                                }
                            }
                            is UiState.Error -> {
                                showError("Ошибка загрузки информации об аллергии: ${state.message}")
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
    
    private fun setupAllergySpinner(allergies: List<Allergy>) {
        val allergyNames = allergies.map { it.name }
        val allergiesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            allergyNames
        )
        binding.autoCompleteAllergy.setAdapter(allergiesAdapter)
        
        binding.autoCompleteAllergy.setOnItemClickListener { _, _, position, _ ->
            selectedAllergy = allergies[position]
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Проверка аллергии
        if (selectedAllergy == null) {
            binding.textInputLayoutAllergy.error = "Выберите аллергию"
            isValid = false
        } else {
            binding.textInputLayoutAllergy.error = null
        }
        
        // Проверка тяжести
        if (binding.autoCompleteSeverity.text.isNullOrBlank()) {
            binding.textInputLayoutSeverity.error = "Выберите тяжесть"
            isValid = false
        } else {
            binding.textInputLayoutSeverity.error = null
        }
        
        // Проверка даты
        if (binding.textInputDate.text.isNullOrBlank()) {
            binding.textInputLayoutDate.error = "Укажите дату"
            isValid = false
        } else {
            binding.textInputLayoutDate.error = null
        }
        
        // Проверка симптомов
        if (selectedSymptoms.isEmpty()) {
            Snackbar.make(binding.root, "Выберите хотя бы один симптом", Snackbar.LENGTH_SHORT).show()
            isValid = false
        }
        
        return isValid
    }

    private fun saveReaction() {
        try {
            val severity = binding.autoCompleteSeverity.text.toString().trim()
            val notes = binding.editTextNotes.text.toString().trim()
            val medication = binding.editTextMedication.text.toString().trim()
            val durationText = binding.editTextDuration.text.toString().trim()
            val duration = if (durationText.isNotEmpty()) durationText.toIntOrNull() else null
            
            val reaction = Reaction(
                id = UUID.randomUUID().toString(),
                allergyId = selectedAllergy?.id ?: "",
                date = selectedDate,
                severity = severity,
                symptoms = selectedSymptoms.toList(),
                notes = notes,
                medication = medication.takeIf { it.isNotEmpty() },
                duration = duration
            )
            
            viewModel.addReaction(reaction)
            
            // Показываем сообщение об успехе и возвращаемся назад
            Snackbar.make(binding.root, "Реакция успешно добавлена", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } catch (e: Exception) {
            Timber.e(e, "Error saving reaction")
            showError("Ошибка при сохранении реакции")
        }
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 