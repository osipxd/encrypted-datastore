package com.example.sample.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

// See: https://m3.material.io/styles/motion/easing-and-duration/tokens-specs
object Motion {
    private const val DurationShortEnter = 150
    private const val DurationShortExit = 100

    private val EasingStandardDecelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
    private val EasingStandardAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    fun <T> standardEnter(duration: Int = DurationShortEnter) = tween<T>(
        durationMillis = duration,
        easing = EasingStandardDecelerate,
    )

    fun <T> standardExit(duration: Int = DurationShortExit) = tween<T>(
        durationMillis = duration,
        easing = EasingStandardAccelerate,
    )
}
