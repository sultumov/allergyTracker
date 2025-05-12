package com.example.allergytracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.allergytracker.databinding.FragmentTestFirebaseBinding
import com.example.allergytracker.ui.util.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TestFirebaseFragment : Fragment() {

    private var _binding: FragmentTestFirebaseBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTestFirebase.setOnClickListener {
            testFirebaseConnection()
        }

        binding.btnAddTestData.setOnClickListener {
            addTestData()
        }
    }

    private fun testFirebaseConnection() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                FirebaseUtils.checkAndDisplayFirebaseStatus(requireContext(), viewLifecycleOwner.lifecycleScope)
                binding.tvFirebaseStatus.text = "Проверка Firebase запущена"
            } catch (e: Exception) {
                binding.tvFirebaseStatus.text = "Ошибка: ${e.message}"
                Timber.e(e, "Firebase connection test error")
            }
        }
    }

    private fun addTestData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val testData = hashMapOf(
                    "name" to "Тестовая аллергия",
                    "timestamp" to System.currentTimeMillis()
                )

                binding.tvFirebaseStatus.text = "Добавление тестовых данных..."

                firestore.collection("test_data")
                    .add(testData)
                    .addOnSuccessListener { documentReference ->
                        binding.tvFirebaseStatus.text = "Данные успешно добавлены с ID: ${documentReference.id}"
                    }
                    .addOnFailureListener { e ->
                        binding.tvFirebaseStatus.text = "Ошибка при добавлении данных: ${e.message}"
                        Timber.e(e, "Error adding test data")
                    }
            } catch (e: Exception) {
                binding.tvFirebaseStatus.text = "Ошибка: ${e.message}"
                Timber.e(e, "Test data addition error")
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 