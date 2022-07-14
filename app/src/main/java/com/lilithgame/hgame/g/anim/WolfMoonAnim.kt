package com.lilithgame.hgame.g.anim

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.doOnEnd

class WolfMoonAnim {
    fun spreadOut(
        topLeft: View,
        topRight: View,
        bottomRight: View,
        bottomLeft: View,
        onMiddle: () -> Unit,
        onEnd: () -> Unit,
    ) {
        ValueAnimator.ofFloat(START_POS, END_POS).apply {
            duration = DURATION
            addUpdateListener {
                val v = it.animatedValue as Float
                topLeft.translationY = -v
                topLeft.translationX = -v
                topLeft.rotation = v
                topRight.translationY = -v
                topRight.translationX = v
                topRight.rotation = v
                bottomLeft.translationY = v
                bottomLeft.translationX = -v
                bottomLeft.rotation = v
                bottomRight.translationY = v
                bottomRight.translationX = v
                bottomRight.rotation = v
            }
            doOnEnd {
                onMiddle()
                spreadIn(
                    topLeft = topLeft,
                    topRight = topRight,
                    bottomRight = bottomRight,
                    bottomLeft = bottomLeft,
                    onEnd = onEnd
                )
            }
            start()
        }
    }

    fun spreadIn(
        topLeft: View,
        topRight: View,
        bottomRight: View,
        bottomLeft: View,
        onEnd: () -> Unit
    ) {
        ValueAnimator.ofFloat(END_POS, START_POS).apply {
            duration = DURATION
            addUpdateListener {
                val v = it.animatedValue as Float
                topLeft.translationY = -v
                topLeft.translationX = -v
                topLeft.rotation = v
                topRight.translationY = -v
                topRight.translationX = v
                topRight.rotation = v
                bottomLeft.translationY = v
                bottomLeft.translationX = -v
                bottomLeft.rotation = v
                bottomRight.translationY = v
                bottomRight.translationX = v
                bottomRight.rotation = v
            }
            doOnEnd {
                onEnd()
            }
            start()
        }
    }

    companion object {
        private const val START_POS = 0f
        private const val END_POS = 800f
        private const val DURATION = 1000L
    }
}