package com.example.allergytracker.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.allergytracker.R
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.databinding.ActivityAddAllergyBinding
import com.example.allergytracker.viewmodel.AllergyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddAllergyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAllergyBinding
    private lateinit var viewModel: AllergyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AllergyViewModel::class.java]

        binding.btnSave.setOnClickListener {
            val symptoms = binding.editSymptoms.text.toString().trim()
            val triggers = binding.editTriggers.text.toString().trim()
            val medication = binding.editMedication.text.toString().trim()

            if (symptoms.isEmpty() || triggers.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val record = AllergyRecord(
                date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                symptoms = symptoms,
                triggers = triggers,
                medication = if (medication.isEmpty()) null else medication
            )

            viewModel.addRecord(record)
            finish()
        }
    }
}