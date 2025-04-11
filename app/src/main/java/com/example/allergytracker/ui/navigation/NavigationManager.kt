package com.example.allergytracker.ui.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.allergytracker.ui.allergy.AllergyDetailsFragmentDirections
import com.example.allergytracker.ui.allergy.AllergyListFragmentDirections
import com.example.allergytracker.ui.allergy.EditAllergyFragmentDirections
import timber.log.Timber

/**
 * Централизованный менеджер навигации для приложения
 */
object NavigationManager {

    /**
     * Навигация к деталям аллергии
     */
    fun navigateToAllergyDetails(fragment: Fragment, allergyId: String) {
        try {
            val navController = fragment.findNavController()
            
            val action = when (fragment.javaClass.simpleName) {
                "AllergyListFragment" -> AllergyListFragmentDirections.actionAllergyListToAllergyDetails(allergyId)
                "ReactionDetailsFragment" -> {
                    // Здесь будет действие из фрагмента деталей реакции к деталям аллергии
                    // Пример: ReactionDetailsFragmentDirections.actionReactionDetailsToAllergyDetails(allergyId)
                    null
                }
                else -> null
            }
            
            action?.let { navController.navigate(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to allergy details with id: $allergyId")
        }
    }

    /**
     * Навигация к экрану добавления аллергии
     */
    fun navigateToAddAllergy(fragment: Fragment) {
        try {
            val navController = fragment.findNavController()
            
            val action = when (fragment.javaClass.simpleName) {
                "AllergyListFragment" -> AllergyListFragmentDirections.actionAllergyListToAddAllergy()
                else -> null
            }
            
            action?.let { navController.navigate(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to add allergy screen")
        }
    }

    /**
     * Навигация к экрану редактирования аллергии
     */
    fun navigateToEditAllergy(fragment: Fragment, allergyId: String) {
        try {
            val navController = fragment.findNavController()
            
            val action = when (fragment.javaClass.simpleName) {
                "AllergyDetailsFragment" -> AllergyDetailsFragmentDirections.actionAllergyDetailsToEditAllergy(allergyId)
                else -> null
            }
            
            action?.let { navController.navigate(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to edit allergy with id: $allergyId")
        }
    }

    /**
     * Навигация к деталям реакции
     */
    fun navigateToReactionDetails(fragment: Fragment, reactionId: String) {
        try {
            val navController = fragment.findNavController()
            
            val action = when (fragment.javaClass.simpleName) {
                "AllergyDetailsFragment" -> AllergyDetailsFragmentDirections.actionAllergyDetailsToReactionDetails(reactionId)
                "ReactionListFragment" -> {
                    // Здесь будет действие из списка реакций к деталям реакции
                    // Пример: ReactionListFragmentDirections.actionReactionListToReactionDetails(reactionId)
                    null
                }
                else -> null
            }
            
            action?.let { navController.navigate(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to reaction details with id: $reactionId")
        }
    }

    /**
     * Навигация к экрану добавления реакции
     */
    fun navigateToAddReaction(fragment: Fragment, allergyId: String? = null) {
        try {
            val navController = fragment.findNavController()
            
            val action = when (fragment.javaClass.simpleName) {
                "AllergyDetailsFragment" -> AllergyDetailsFragmentDirections.actionAllergyDetailsToAddReaction(allergyId)
                "ReactionListFragment" -> {
                    // Здесь будет действие из списка реакций к добавлению реакции
                    // Пример: ReactionListFragmentDirections.actionReactionListToAddReaction()
                    null
                }
                else -> null
            }
            
            action?.let { navController.navigate(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to add reaction screen")
        }
    }

    /**
     * Универсальная навигация назад
     */
    fun navigateBack(navController: NavController): Boolean {
        return try {
            navController.navigateUp()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error navigating back")
            false
        }
    }
} 