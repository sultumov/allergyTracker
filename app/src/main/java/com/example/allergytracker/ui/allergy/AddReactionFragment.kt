package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.allergytracker.databinding.FragmentAddReactionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReactionFragment : Fragment() {
    private var _binding: FragmentAddReactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllergyViewModel by viewModels()

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
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            saveReaction()
        }
    }

    private fun saveReaction() {
        val severity = binding.severitySpinner.selectedItem.toString()
        val symptoms = binding.symptomsEditText.text.toString()
        val notes = binding.notesEditText.text.toString()

        if (symptoms.isBlank()) {
            binding.symptomsEditText.error = "Symptoms are required"
            return
        }

        viewModel.addReaction(severity, symptoms, notes)
    }

    private fun observeViewModel() {
        viewModel.reactionAdded.observe(viewLifecycleOwner) { added ->
            if (added) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 