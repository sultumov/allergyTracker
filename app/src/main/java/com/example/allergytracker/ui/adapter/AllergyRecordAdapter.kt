package com.example.allergytracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.databinding.ItemAllergyRecordBinding
import com.google.android.material.snackbar.Snackbar

class AllergyRecordAdapter(
    private val onDeleteClick: (AllergyRecord) -> Unit
) : ListAdapter<AllergyRecord, AllergyRecordAdapter.AllergyRecordViewHolder>(AllergyRecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyRecordViewHolder {
        val binding = ItemAllergyRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllergyRecordViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: AllergyRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AllergyRecordViewHolder(
        private val binding: ItemAllergyRecordBinding,
        private val onDeleteClick: (AllergyRecord) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AllergyRecord) {
            binding.apply {
                tvDate.text = record.date
                tvSymptoms.text = record.symptoms
                tvTriggers.text = record.triggers
                tvMedication.text = record.medication ?: "Не указано"
                
                btnDelete.setOnClickListener {
                    onDeleteClick(record)
                }
            }
        }
    }

    private class AllergyRecordDiffCallback : DiffUtil.ItemCallback<AllergyRecord>() {
        override fun areItemsTheSame(oldItem: AllergyRecord, newItem: AllergyRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AllergyRecord, newItem: AllergyRecord): Boolean {
            return oldItem == newItem
        }
    }
} 