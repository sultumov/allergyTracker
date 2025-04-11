package com.example.allergytracker.ui.allergy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.allergytracker.databinding.ItemAllergyBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.common.BaseFilterAdapter
import com.example.allergytracker.ui.common.GenericDiffCallback
import com.example.allergytracker.util.AnimationUtils
import com.example.allergytracker.util.DateUtils
import timber.log.Timber

class AllergyAdapter(
    private val onItemClick: (Allergy) -> Unit,
    private val onLongClick: (Allergy) -> Boolean
) : BaseFilterAdapter<Allergy, AllergyAdapter.AllergyViewHolder>(
    GenericDiffCallback(
        getItemIdFunction = { it.id },
        areContentsTheSameFunction = { old, new -> old == new }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val binding = ItemAllergyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllergyViewHolder(binding, onItemClick, onLongClick)
    }
    
    override fun onViewAttachedToWindow(holder: AllergyViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            AnimationUtils.playListItemAnimation(holder.itemView)
        } catch (e: Exception) {
            Timber.e(e, "Error applying animation")
        }
    }

    override fun filterItem(item: Allergy, constraint: String): Boolean {
        return item.name.lowercase().contains(constraint) ||
               item.category.lowercase().contains(constraint) ||
               item.description.lowercase().contains(constraint) ||
               item.severity.lowercase().contains(constraint)
    }

    class AllergyViewHolder(
        private val binding: ItemAllergyBinding,
        onItemClick: (Allergy) -> Unit,
        onItemLongClick: (Allergy) -> Boolean
    ) : BaseViewHolder<Allergy>(binding.root, onItemClick, onItemLongClick) {

        override fun bindItem(allergy: Allergy) {
            try {
                binding.apply {
                    textAllergyName.text = allergy.name
                    textAllergyCategory.text = allergy.category
                    textAllergySeverity.text = allergy.severity
                    
                    textAllergyDate.text = DateUtils.formatDate(allergy.createdAt)
                    
                    // Можно добавить визуальное обозначение активности
                    root.alpha = if (allergy.isActive) 1.0f else 0.5f
                }
            } catch (e: Exception) {
                Timber.e(e, "Error binding allergy data: $allergy")
            }
        }
    }
} 