package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentAddAllergyBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class AddAllergyFragment : Fragment() {
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
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupListeners()
            observeViewModel()
        } catch (e: Exception) {
            Timber.e(e, "Error in onViewCreated")
            showError("Ошибка при инициализации экрана")
        }
    }
    
    private fun setupListeners() {
        binding.saveButton.setOnClickListener {
            saveAllergy()
        }
    }
    
    private fun saveAllergy() {
        try {
            val name = binding.allergyNameInput.text.toString().trim()
            if (name.isEmpty()) {
                binding.allergyNameInput.error = "Введите название аллергии"
                return
            }
            
            val description = binding.allergyDescriptionInput.text.toString().trim()
            val severity = binding.severitySlider.value.toInt().toString()
            
            val allergy = Allergy(
                id = System.currentTimeMillis(),
                name = name,
                description = description,
                severity = severity,
                category = "Общее", // Можно добавить выбор категории
                isActive = true,
                createdAt = Date()
            )
            
            viewModel.addAllergy(allergy)
        } catch (e: Exception) {
            Timber.e(e, "Error saving allergy")
            showError("Ошибка при сохранении данных")
        }
    }
    
    private fun observeViewModel() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.saveButton.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    Toast.makeText(requireContext(), "Аллергия сохранена", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    showError(state.message)
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                }
            }
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 