package com.kyant.capsule

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal class G2ContinuityData(
    val circleFraction: Double,
    val extendedFraction: Double,
    val bezierCurvatureScale: Double,
    val circleCurvatureScale: Double
) {

    val circleRadians = HalfPI * circleFraction
    val bezierRadians = (HalfPI - circleRadians) * 0.5

    private val sin = sin(bezierRadians)
    private val cos = cos(bezierRadians)
    private val halfTan = sin / (1.0 + cos)

    private val bezier =
        if (bezierCurvatureScale == 1.0 && circleCurvatureScale == 1.0) {
            // fast path
            CubicBezier(
                Point(-extendedFraction, 0.0),
                Point((1.0 - 1.5 / (1.0 + cos)) * halfTan, 0.0),
                Point(halfTan, 0.0),
                Point(sin, 1.0 - cos)
            )
        } else {
            val endTangent =
                if (circleFraction > 0.0) {
                    Segment.IntrinsicArc(
                        from = Point(sin, 1.0 - cos),
                        to = Point(
                            sin(bezierRadians + circleRadians),
                            1.0 - cos(bezierRadians + circleRadians)
                        ),
                        radius = 1.0 / circleCurvatureScale
                    ).unitTangentAt(0.0)
                } else {
                    Point(1.0 / sqrt(2.0), 1.0 / sqrt(2.0))
                }
            CubicBezier.generateG2ContinuousBezier(
                start = Point(-extendedFraction, 0.0),
                end = Point(sin, 1.0 - cos),
                startTangent = Point(1.0, 0.0),
                endTangent = endTangent,
                startCurvature = 0.0,
                endCurvature = bezierCurvatureScale
            )
        }

    private var _capsuleBezier: CubicBezier? = null

    private val capsuleBezier
        get() = _capsuleBezier
            ?: CubicBezier(
                Point.Zero,
                Point((1.0 - 1.5 / (1.0 + cos)) * halfTan, 0.0),
                Point(halfTan, 0.0),
                Point(sin, 1.0 - cos)
            ).also {
                _capsuleBezier = it
            }

    fun getBezier(
        extendedFraction: Double,
        bezierCurvatureScale: Double,
        circleCurvatureScale: Double
    ): CubicBezier {
        if (extendedFraction == this.extendedFraction &&
            bezierCurvatureScale == this.bezierCurvatureScale &&
            circleCurvatureScale == this.circleCurvatureScale
        ) {
            return bezier
        }
        if (extendedFraction == 0.0) {
            return capsuleBezier
        }
        return G2ContinuityData(
            circleFraction = circleFraction,
            extendedFraction = extendedFraction,
            bezierCurvatureScale = bezierCurvatureScale,
            circleCurvatureScale = circleCurvatureScale
        ).bezier
    }
}
