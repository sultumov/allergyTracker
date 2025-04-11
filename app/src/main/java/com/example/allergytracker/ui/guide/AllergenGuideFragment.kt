package com.example.allergytracker.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.databinding.FragmentAllergenGuideBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllergenGuideFragment : Fragment() {
    private var _binding: FragmentAllergenGuideBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllergenGuideViewModel by viewModels()
    private val allergenAdapter = AllergenAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergenGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.allergensList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allergenAdapter
        }
    }

    private fun setupSearch() {
        binding.searchInput.editText?.let { editText ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setupSearch(editText)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AllergenGuideUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AllergenGuideUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        allergenAdapter.submitList(state.allergens)
                    }
                    is AllergenGuideUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 