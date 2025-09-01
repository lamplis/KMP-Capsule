package com.kyant.capsule.continuities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Outline
import com.kyant.capsule.Continuity
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.buildPathSegments
import com.kyant.capsule.path.toPath

@Immutable
data object G0Continuity : Continuity(isComplex = false) {

    override fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        return buildPathSegments {
            moveTo(0.0, topLeft)
            if (topLeft > 0.0) {
                lineTo(topLeft, 0.0)
            }
            lineTo(width - topRight, 0.0)
            if (topRight > 0.0) {
                lineTo(width, topRight)
            }
            lineTo(width, height - bottomRight)
            if (bottomRight > 0.0) {
                lineTo(width - bottomRight, height)
            }
            lineTo(bottomLeft, height)
            if (bottomLeft > 0.0) {
                lineTo(0.0, height - bottomLeft)
            }
            close()
        }
    }

    override fun createCirclePathSegments(size: Double): PathSegments {
        val radius = size * 0.5
        return buildPathSegments {
            moveTo(0.0, radius)
            lineTo(radius, 0.0)
            lineTo(size, radius)
            close()
        }
    }

    override fun createCircleOutline(size: Float): Outline {
        val radius = size * 0.5
        val path = createCirclePathSegments(radius).toPath()
        return Outline.Generic(path)
    }

    override fun lerp(stop: Continuity, fraction: Double): Continuity {
        return when (stop) {
            is G0Continuity,
            is G1Continuity,
            is G2Continuity -> if (fraction < 0.5) this else stop

            else -> stop.lerp(this, 1f - fraction)
        }
    }
}
