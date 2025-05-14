package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentAllergyListBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.state.UiState
import com.example.allergytracker.util.safeNavigate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AllergyListFragment : Fragment() {
    private var _binding: FragmentAllergyListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AllergyViewModel by viewModels()
    private lateinit var adapter: AllergyAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            setupRecyclerView()
            setupSearch()
            setupFilters()
            observeViewModel()
            setupListeners()
        } catch (e: Exception) {
            Timber.e(e, "Error in onViewCreated")
            Toast.makeText(requireContext(), "Произошла ошибка при загрузке данных", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = AllergyAdapter(
            onItemClick = { allergy ->
                findNavController().safeNavigate(
                    AllergyListFragmentDirections.actionAllergyListFragmentToAllergyDetailFragment(allergy.id)
                )
            },
            onLongClick = { allergy ->
                showDeleteDialog(allergy)
                true
            }
        )
        
        binding.allergiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AllergyListFragment.adapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.item_animation
            )
        }
    }
    
    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText ?: "")
                return true
            }
        })
    }
    
    private fun setupFilters() {
        binding.filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val filterType = when (checkedId) {
                R.id.chipAll -> AllergyViewModel.FilterType.ALL
                R.id.chipActive -> AllergyViewModel.FilterType.ACTIVE
                R.id.chipSevere -> AllergyViewModel.FilterType.SEVERE
                else -> AllergyViewModel.FilterType.ALL
            }
            viewModel.setFilterType(filterType)
        }
    }
    
    private fun setupListeners() {
        binding.addAllergyButton.setOnClickListener {
            findNavController().safeNavigate(
                AllergyListFragmentDirections.actionAllergyListFragmentToAddAllergyFragment()
            )
        }
        
        binding.testFirebaseButton.setOnClickListener {
            findNavController().safeNavigate(
                AllergyListFragmentDirections.actionAllergyListFragmentToTestFirebaseFragment()
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.allergies.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.allergiesRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    if (state.data.isEmpty()) {
                        binding.allergiesRecyclerView.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    } else {
                        binding.allergiesRecyclerView.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                        adapter.submitList(state.data)
                        binding.allergiesRecyclerView.scheduleLayoutAnimation()
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.allergiesRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.emptyView.text = state.message
                    Timber.e("Error loading allergies: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showDeleteDialog(allergy: Allergy) {
        // Обработка удаления с подтверждением
        viewModel.deleteAllergy(allergy)
        showUndoSnackbar(allergy)
    }
    
    private fun showUndoSnackbar(allergy: Allergy) {
        Snackbar.make(
            binding.root,
            "Аллергия удалена",
            Snackbar.LENGTH_LONG
        ).setAction("Отменить") {
            viewModel.addAllergy(allergy)
        }.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 