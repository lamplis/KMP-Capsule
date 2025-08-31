package com.kyant.capsule

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.toPath

@Immutable
interface Continuity {

    val isValid: Boolean

    fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments

    fun createRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        val path =
            createRoundedRectanglePathSegments(
                width = size.width.toDouble(),
                height = size.height.toDouble(),
                topLeft = topLeft.toDouble(),
                topRight = topRight.toDouble(),
                bottomRight = bottomRight.toDouble(),
                bottomLeft = bottomLeft.toDouble()
            ).toPath()
        return Outline.Generic(path)
    }

    fun createHorizontalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.height * 0.5f
        return createRoundedRectangleOutline(
            size = size,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    fun createVerticalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.width * 0.5f
        return createRoundedRectangleOutline(
            size = size,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    fun lerp(stop: Continuity, fraction: Double): Continuity

    companion object {

        @Stable
        val Default: Continuity = G2Continuity()
    }
}
