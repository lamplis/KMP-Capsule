package com.kyant.capsule.continuities

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import com.kyant.capsule.core.CubicBezier
import com.kyant.capsule.core.Point
import com.kyant.capsule.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Immutable
data class G2ContinuityConfig(
    @param:FloatRange(from = 0.0) val extendedFraction: Double,
    @param:FloatRange(from = 0.0, to = 1.0) val arcFraction: Double,
    @param:FloatRange(from = 0.0) val bezierCurvatureScale: Double,
    @param:FloatRange(from = 0.0, fromInclusive = false) val arcCurvatureScale: Double
) {

    private var _bezier: CubicBezier? = null
    internal val bezier: CubicBezier
        get() = _bezier ?: createBaseBezier().also { _bezier = it }

    private fun createBaseBezier(): CubicBezier {
        val arcRadians = PI * 0.5 * arcFraction
        val bezierRadians = (PI * 0.5 - arcRadians) * 0.5
        val sin = sin(bezierRadians)
        val cos = cos(bezierRadians)

        return if (bezierCurvatureScale == 1.0 && arcCurvatureScale == 1.0) {
            val halfTan = sin / (1.0 + cos)
            CubicBezier(
                Point(-extendedFraction, 0.0),
                Point((1.0 - 1.5 / (1.0 + cos)) * halfTan, 0.0),
                Point(halfTan, 0.0),
                Point(sin, 1.0 - cos)
            )
        } else {
            val endTangent =
                if (arcFraction > 0.0 && arcCurvatureScale > 0.0) {
                    getUnitTangentAtStartOfArc(
                        from = Point(sin, 1.0 - cos),
                        to = Point(
                            sin(bezierRadians + arcRadians),
                            1.0 - cos(bezierRadians + arcRadians)
                        ),
                        radius = 1.0 / arcCurvatureScale
                    )
                } else {
                    Point(1.0 / sqrt(2.0), 1.0 / sqrt(2.0))
                }

            /* solved using G2 continuity conditions:
                start = Point(-extendedFraction, 0.0)
                end = Point(sin, 1.0 - cos)
                startTangent = Point(1.0, 0.0)
                endTangent = endTangent
                startCurvature = 0.0
                endCurvature = bezierCurvatureScale
            */
            val b1 = 1.5 * bezierCurvatureScale
            val a2 = endTangent.y
            val dx = sin - (-extendedFraction)
            val dy = 1.0 - cos
            val a3 = -dy
            val b3 = dy * endTangent.x - dx * endTangent.y
            CubicBezier(
                Point(-extendedFraction, 0.0),
                Point(-extendedFraction + (-b3 / a2 - b1 * a3 * a3 / a2 / a2 / a2), 0.0),
                Point(sin - dy * (endTangent.x / endTangent.y), 0.0),
                Point(sin, 1.0 - cos)
            )
        }
    }

    companion object {

        val RoundedRectangle: G2ContinuityConfig =
            G2ContinuityConfig(
                extendedFraction = 1.0,
                arcFraction = 0.25,
                bezierCurvatureScale = 1.06,
                arcCurvatureScale = 1.06
            )

        val Capsule: G2ContinuityConfig =
            G2ContinuityConfig(
                extendedFraction = 0.75,
                arcFraction = 0.20,
                bezierCurvatureScale = 1.0,
                arcCurvatureScale = 1.0
            )

        val G1Equivalent: G2ContinuityConfig =
            G2ContinuityConfig(
                extendedFraction = 0.0,
                arcFraction = 1.0,
                bezierCurvatureScale = 1.0,
                arcCurvatureScale = 1.0
            )
    }
}

fun lerp(start: G2ContinuityConfig, stop: G2ContinuityConfig, fraction: Double): G2ContinuityConfig {
    return G2ContinuityConfig(
        extendedFraction = lerp(start.extendedFraction, stop.extendedFraction, fraction),
        arcFraction = lerp(start.arcFraction, stop.arcFraction, fraction),
        bezierCurvatureScale = lerp(start.bezierCurvatureScale, stop.bezierCurvatureScale, fraction),
        arcCurvatureScale = lerp(start.arcCurvatureScale, stop.arcCurvatureScale, fraction)
    )
}

private fun getUnitTangentAtStartOfArc(from: Point, to: Point, radius: Double): Point {
    val mid = (from + to) * 0.5
    val dir = to - from
    val len = sqrt(dir.x * dir.x + dir.y * dir.y)
    val height = sqrt(radius * radius - (len * 0.5) * (len * 0.5))
    val norm = Point(-dir.y / len, dir.x / len)
    val center = mid + norm * height * if (radius > 0) 1.0 else -1.0
    return Point(
        -(from.y - center.y) / radius,
        (from.x - center.x) / radius
    )
}
