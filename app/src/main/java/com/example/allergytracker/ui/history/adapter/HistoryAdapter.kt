package com.example.allergytracker.ui.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.data.model.HistoryItem
import com.example.allergytracker.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onItemClick: (HistoryItem) -> Unit = {}
) : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            Log.e("HistoryAdapter", "Error binding history item", e)
        }
    }

    class HistoryViewHolder(
        private val binding: ItemHistoryBinding,
        private val onItemClick: (HistoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onItemClick(getItem(position))
                    } catch (e: Exception) {
                        Log.e("HistoryAdapter", "Error handling item click", e)
                    }
                }
            }
        }

        fun bind(item: HistoryItem) {
            binding.apply {
                productNameText.text = item.productName
                scanDateText.text = dateFormat.format(item.scanDate)

                with(glutenChip) {
                    visibility = if (item.containsGluten) View.VISIBLE else View.GONE
                    isChecked = item.containsGluten
                }

                with(lactoseChip) {
                    visibility = if (item.containsLactose) View.VISIBLE else View.GONE
                    isChecked = item.containsLactose
                }

                with(nutsChip) {
                    visibility = if (item.containsNuts) View.VISIBLE else View.GONE
                    isChecked = item.containsNuts
                }
            }
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
} 