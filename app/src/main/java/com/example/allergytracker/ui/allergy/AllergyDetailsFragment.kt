package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.R
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.databinding.FragmentAllergyDetailsBinding
import com.example.allergytracker.ui.viewmodel.AllergyTrackerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllergyDetailsFragment : Fragment() {
    private var _binding: FragmentAllergyDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllergyTrackerViewModel by viewModels()
    private val args: AllergyDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        loadAllergyDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            // TODO: Implement edit functionality
            Snackbar.make(binding.root, "Редактирование будет добавлено позже", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление аллергии")
            .setMessage("Вы уверены, что хотите удалить эту аллергию?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteAllergy()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteAllergy() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.deleteAllergy(args.allergyId)
                findNavController().navigateUp()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Ошибка при удалении: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                updateLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    showError(it)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.nestedScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun loadAllergyDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.allergies.collectLatest { allergies ->
                    val allergy = allergies.find { it.id == args.allergyId }
                    if (allergy != null) {
                        updateUI(allergy)
                    } else {
                        showError("Аллергия не найдена")
                        findNavController().navigateUp()
                    }
                }
            } catch (e: Exception) {
                showError("Ошибка при загрузке данных: ${e.message}")
                findNavController().navigateUp()
            }
        }
    }

    private fun updateUI(allergy: Allergy) {
        try {
            binding.apply {
                tvAllergyName.text = allergy.name
                tvAllergyCategory.text = allergy.category.toString()
                tvSeverity.text = allergy.severity.toString()
                tvDescription.text = allergy.description
            }
        } catch (e: Exception) {
            showError("Ошибка при отображении данных: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 