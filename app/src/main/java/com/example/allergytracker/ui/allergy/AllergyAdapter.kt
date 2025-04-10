package com.example.allergytracker.ui.allergy

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.R
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.databinding.ItemAllergyBinding

class AllergyAdapter(
    private val onItemClick: (Allergy) -> Unit,
    private val onItemLongClick: (Allergy) -> Unit = {}
) : ListAdapter<Allergy, AllergyAdapter.AllergyViewHolder>(AllergyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val binding = ItemAllergyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllergyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            Log.e("AllergyAdapter", "Error binding allergy item at position $position", e)
        }
    }

    override fun onViewAttachedToWindow(holder: AllergyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
        )
    }

    inner class AllergyViewHolder(
        private val binding: ItemAllergyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onItemClick(getItem(position))
                    } catch (e: Exception) {
                        Log.e("AllergyAdapter", "Error handling item click at position $position", e)
                    }
                }
            }

            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onItemLongClick(getItem(position))
                        true
                    } catch (e: Exception) {
                        Log.e("AllergyAdapter", "Error handling long click at position $position", e)
                        false
                    }
                } else {
                    false
                }
            }
        }

        fun bind(allergy: Allergy) {
            try {
                binding.apply {
                    tvAllergyName.text = allergy.name
                    tvAllergyCategory.text = allergy.category.toString()
                    tvSeverity.text = allergy.severity.toString()
                    
                    if (allergy.description.isNotEmpty()) {
                        tvNotes.text = allergy.description
                        tvNotes.visibility = View.VISIBLE
                    } else {
                        tvNotes.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.e("AllergyAdapter", "Error binding allergy: ${allergy.name}", e)
            }
        }
    }

    private class AllergyDiffCallback : DiffUtil.ItemCallback<Allergy>() {
        override fun areItemsTheSame(oldItem: Allergy, newItem: Allergy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Allergy, newItem: Allergy): Boolean {
            return oldItem == newItem
        }
    }
} 