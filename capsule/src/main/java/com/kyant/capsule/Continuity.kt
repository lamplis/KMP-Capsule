package com.kyant.capsule

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.core.Point
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.buildPathSegments
import com.kyant.capsule.path.toPath
import kotlin.math.PI

@Immutable
abstract class Continuity(val isComplex: Boolean) {

    protected abstract fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments

    protected open fun createCirclePathSegments(size: Double): PathSegments {
        val radius = size * 0.5
        return buildPathSegments {
            moveTo(size, radius)
            arcTo(
                center = Point(radius, radius),
                radius = radius,
                startAngle = 0.0,
                sweepAngle = PI * 2.0
            )
        }
    }

    protected open fun createRoundedRectangleOutline(
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

    protected open fun createCircleOutline(size: Float): Outline {
        val radius = size * 0.5f
        return Outline.Rounded(
            RoundRect(
                rect = Rect(0f, 0f, size, size),
                radiusX = radius,
                radiusY = radius
            )
        )
    }

    protected open fun createHorizontalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val cornerRadius = width * 0.5
        return createRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    protected open fun createVerticalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val cornerRadius = height * 0.5
        return createRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    protected open fun createHorizontalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.height * 0.5f
        return createRoundedRectangleOutline(
            size = size,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    protected open fun createVerticalCapsuleOutline(size: Size): Outline {
        val cornerRadius = size.width * 0.5f
        return createRoundedRectangleOutline(
            size = size,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    fun createGeneralRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        // capsule or circle
        if (topLeft == width * 0.5 && topLeft == topRight && bottomLeft == bottomRight) {
            return when {
                width > height -> createHorizontalCapsulePathSegments(width, height)
                width < height -> createVerticalCapsulePathSegments(width, height)
                else -> createCirclePathSegments(width)
            }
        }

        // continuous rounded rectangle
        return createRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }

    fun createGeneralRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        // capsule or circle
        val width = size.width
        val height = size.height
        if (topLeft == width * 0.5f && topLeft == topRight && bottomLeft == bottomRight) {
            return when {
                width > height -> createHorizontalCapsuleOutline(size)
                width < height -> createVerticalCapsuleOutline(size)
                else -> createCircleOutline(size.width)
            }
        }

        // continuous rounded rectangle
        return createRoundedRectangleOutline(
            size = size,
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }

    fun createGeneralCapsulePathSegments(width: Double, height: Double): PathSegments =
        when {
            width > height -> createHorizontalCapsulePathSegments(width, height)
            width < height -> createVerticalCapsulePathSegments(width, height)
            else -> createCirclePathSegments(width)
        }

    fun createGeneralCapsuleOutline(size: Size): Outline =
        when {
            size.width > size.height -> createHorizontalCapsuleOutline(size)
            size.width < size.height -> createVerticalCapsuleOutline(size)
            else -> createCircleOutline(size.width)
        }

    abstract fun lerp(stop: Continuity, fraction: Double): Continuity

    companion object {

        @Stable
        val Default: Continuity = G2Continuity()
    }
}
