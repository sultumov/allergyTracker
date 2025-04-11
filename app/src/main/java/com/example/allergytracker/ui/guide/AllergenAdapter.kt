package com.example.allergytracker.ui.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.data.model.Allergen
import com.example.allergytracker.databinding.ItemAllergenBinding

class AllergenAdapter : ListAdapter<Allergen, AllergenAdapter.AllergenViewHolder>(AllergenDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergenViewHolder {
        val binding = ItemAllergenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllergenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllergenViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AllergenViewHolder(
        private val binding: ItemAllergenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(allergen: Allergen) {
            binding.apply {
                allergenName.text = allergen.name
                allergenDescription.text = allergen.description
                commonSources.text = allergen.commonSources.joinToString(", ")
            }
        }
    }

    private class AllergenDiffCallback : DiffUtil.ItemCallback<Allergen>() {
        override fun areItemsTheSame(oldItem: Allergen, newItem: Allergen): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Allergen, newItem: Allergen): Boolean {
            return oldItem == newItem
        }
    }
} 