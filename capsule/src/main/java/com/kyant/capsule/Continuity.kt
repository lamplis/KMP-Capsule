package com.kyant.capsule

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline

@Immutable
interface Continuity {

    val hasSmoothness: Boolean

    fun createRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline

    fun createHorizontalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.height * 0.5f
        return createRoundedRectangleOutline(
            size,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius
        )
    }

    fun createVerticalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.width * 0.5f
        return createRoundedRectangleOutline(
            size,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius
        )
    }

    fun lerp(stop: Continuity, fraction: Float): Continuity

    companion object {

        @Stable
        val Default: Continuity = G3Continuity()
    }
}
