package com.example.allergytracker.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.R
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.databinding.ItemAllergyRecordBinding

class ReactionAdapter(
    private val onItemClick: (AllergyRecord) -> Unit
) : ListAdapter<AllergyRecord, ReactionAdapter.ReactionViewHolder>(ReactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val binding = ItemAllergyRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            Log.e("ReactionAdapter", "Error binding reaction item at position $position", e)
        }
    }

    override fun onViewAttachedToWindow(holder: ReactionViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
        )
    }

    inner class ReactionViewHolder(
        private val binding: ItemAllergyRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onItemClick(getItem(position))
                    } catch (e: Exception) {
                        Log.e("ReactionAdapter", "Error handling item click at position $position", e)
                    }
                }
            }
        }

        fun bind(record: AllergyRecord) {
            try {
                binding.apply {
                    tvDate.text = record.date
                    tvSymptoms.text = record.symptoms
                    tvTriggers.text = record.triggers
                    tvMedication.text = record.medication ?: "Не указано"
                }
            } catch (e: Exception) {
                Log.e("ReactionAdapter", "Error binding reaction: ${record.id}", e)
            }
        }
    }

    private class ReactionDiffCallback : DiffUtil.ItemCallback<AllergyRecord>() {
        override fun areItemsTheSame(oldItem: AllergyRecord, newItem: AllergyRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AllergyRecord, newItem: AllergyRecord): Boolean {
            return oldItem == newItem
        }
    }
} 