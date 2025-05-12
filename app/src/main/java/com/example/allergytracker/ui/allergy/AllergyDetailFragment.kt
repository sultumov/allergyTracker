package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentAllergyDetailBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.state.UiState
import com.example.allergytracker.util.DateUtils
import com.example.allergytracker.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AllergyDetailFragment : Fragment(), MenuProvider {
    private var _binding: FragmentAllergyDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AllergyViewModel by viewModels()
    private val args: AllergyDetailFragmentArgs by navArgs()
    
    private var currentAllergy: Allergy? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupMenu()
            observeViewModel()
            loadData()
        } catch (e: Exception) {
            Timber.e(e, "Error in onViewCreated")
            showError("Ошибка при загрузке данных")
        }
    }
    
    private fun setupMenu() {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    private fun loadData() {
        viewModel.getAllergyById(args.allergyId)
    }
    
    private fun observeViewModel() {
        viewModel.allergyDetails.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    displayAllergyDetails(state.data)
                    currentAllergy = state.data
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(state.message)
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
    
    private fun displayAllergyDetails(allergy: Allergy) {
        binding.apply {
            allergyName.text = allergy.name
            allergyCategory.text = "Категория: ${allergy.category}"
            allergySeverity.text = "Тяжесть: ${allergy.severity}"
            allergyDescription.text = allergy.description
            allergyDate.text = "Добавлено: ${DateUtils.formatDate(allergy.createdAt)}"
            allergyStatus.text = if (allergy.isActive) "Активная" else "Неактивная"
            
            // Настраиваем цвет для статуса в зависимости от активности
            allergyStatus.setBackgroundResource(
                if (allergy.isActive) R.drawable.status_active_background 
                else R.drawable.status_inactive_background
            )
            
            // Настраиваем цвет для тяжести в зависимости от значения
            allergySeverity.setBackgroundResource(
                when (allergy.severity) {
                    "1", "2" -> R.drawable.severity_low_background
                    "3" -> R.drawable.severity_medium_background
                    "4", "5" -> R.drawable.severity_high_background
                    else -> R.drawable.severity_medium_background
                }
            )
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_detail, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_edit -> {
                currentAllergy?.let { allergy ->
                    findNavController().safeNavigate(
                        AllergyDetailFragmentDirections.actionAllergyDetailFragmentToEditAllergyFragment(allergy.id)
                    )
                }
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> false
        }
    }
    
    private fun showDeleteConfirmationDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удаление аллергии")
            .setMessage("Вы уверены, что хотите удалить эту аллергию?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteAllergy()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun deleteAllergy() {
        currentAllergy?.let { allergy ->
            viewModel.deleteAllergy(allergy)
            Toast.makeText(requireContext(), "Аллергия удалена", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 