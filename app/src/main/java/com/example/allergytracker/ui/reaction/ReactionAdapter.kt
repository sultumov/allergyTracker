package com.example.allergytracker.ui.reaction

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.allergytracker.databinding.ItemReactionBinding
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.ui.common.BaseAdapter
import com.example.allergytracker.ui.common.GenericDiffCallback
import com.example.allergytracker.util.AnimationUtils
import com.example.allergytracker.util.DateUtils
import timber.log.Timber

class ReactionAdapter(
    private val onItemClick: (Reaction) -> Unit,
    private val onLongClick: (Reaction) -> Boolean
) : BaseAdapter<Reaction, ReactionAdapter.ReactionViewHolder>(
    GenericDiffCallback(
        getItemIdFunction = { it.id },
        areContentsTheSameFunction = { old, new -> old == new }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val binding = ItemReactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReactionViewHolder(binding, onItemClick, onLongClick)
    }

    override fun onViewAttachedToWindow(holder: ReactionViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            AnimationUtils.playListItemAnimation(holder.itemView)
        } catch (e: Exception) {
            Timber.e(e, "Error applying animation")
        }
    }

    class ReactionViewHolder(
        private val binding: ItemReactionBinding,
        onItemClick: (Reaction) -> Unit,
        onItemLongClick: (Reaction) -> Boolean
    ) : BaseAdapter.BaseViewHolder<Reaction>(binding.root, onItemClick, onItemLongClick) {

        override fun bindItem(reaction: Reaction) {
            try {
                binding.apply {
                    // Форматирование даты
                    textDate.text = DateUtils.formatDateTime(reaction.date)
                    
                    // Установка тяжести
                    textSeverity.text = reaction.severity
                    
                    // Симптомы (преобразуем список в строку)
                    textSymptoms.text = reaction.symptoms.joinToString(", ")
                    
                    // Дополнительная информация
                    if (reaction.notes.isNotEmpty()) {
                        textNotes.text = reaction.notes
                    } else {
                        textNotes.text = "Нет комментариев"
                    }
                    
                    // Лекарство
                    if (reaction.medication != null && reaction.medication.isNotEmpty()) {
                        textMedication.text = "Лекарство: ${reaction.medication}"
                    } else {
                        textMedication.text = "Без лекарств"
                    }
                    
                    // Продолжительность
                    if (reaction.duration != null) {
                        textDuration.text = "Продолжительность: ${reaction.duration} мин."
                    } else {
                        textDuration.text = "Продолжительность не указана"
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error binding reaction data: $reaction")
            }
        }
    }
} 