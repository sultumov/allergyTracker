package com.example.allergytracker.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.example.allergytracker.R
import timber.log.Timber

/**
 * Утилитный класс для работы с анимациями
 */
object AnimationUtils {

    /**
     * Анимация появления (fade in)
     */
    fun fadeIn(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        try {
            if (view.visibility == View.VISIBLE && view.alpha == 1f) {
                onEnd?.invoke()
                return
            }
            
            view.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            onEnd?.invoke()
                        }
                    })
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in fadeIn animation")
            // В случае ошибки, показываем View без анимации
            view.visibility = View.VISIBLE
            view.alpha = 1f
            onEnd?.invoke()
        }
    }

    /**
     * Анимация исчезновения (fade out)
     */
    fun fadeOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        try {
            if (view.visibility == View.GONE || view.alpha == 0f) {
                onEnd?.invoke()
                return
            }
            
            view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                        onEnd?.invoke()
                    }
                })
        } catch (e: Exception) {
            Timber.e(e, "Error in fadeOut animation")
            // В случае ошибки, скрываем View без анимации
            view.visibility = View.GONE
            view.alpha = 0f
            onEnd?.invoke()
        }
    }

    /**
     * Анимация для элементов списка
     */
    fun playListItemAnimation(view: View) {
        try {
            val animation = android.view.animation.AnimationUtils.loadAnimation(
                view.context,
                R.anim.item_animation
            )
            view.startAnimation(animation)
        } catch (e: Exception) {
            Timber.e(e, "Error in playListItemAnimation")
        }
    }

    /**
     * Переключение видимости с анимацией
     */
    fun toggleVisibilityWithAnimation(view: View, show: Boolean, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        if (show) {
            fadeIn(view, duration, onEnd)
        } else {
            fadeOut(view, duration, onEnd)
        }
    }

    /**
     * Анимация изменения высоты view
     */
    fun changeHeight(view: View, fromHeight: Int, toHeight: Int, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        try {
            val anim = android.animation.ValueAnimator.ofInt(fromHeight, toHeight)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            }
            anim.duration = duration
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            anim.start()
        } catch (e: Exception) {
            Timber.e(e, "Error in changeHeight animation")
            // В случае ошибки, меняем высоту без анимации
            val layoutParams = view.layoutParams
            layoutParams.height = toHeight
            view.layoutParams = layoutParams
            onEnd?.invoke()
        }
    }
    
    /**
     * Анимация для загрузки/обновления контента
     */
    fun crossFade(contentView: View, loadingView: View, duration: Long = 300) {
        fadeIn(contentView, duration)
        fadeOut(loadingView, duration)
    }
} 