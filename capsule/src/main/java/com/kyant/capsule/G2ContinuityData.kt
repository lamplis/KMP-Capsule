package com.kyant.capsule

import kotlin.math.cos
import kotlin.math.sin

internal class G2ContinuityData(
    val circleFraction: Float,
    val extendedFraction: Float
) {

    val circleRadians = HalfPI * circleFraction
    val bezierRadians = (HalfPI - circleRadians) * 0.5

    val sin = sin(bezierRadians)
    val cos = cos(bezierRadians)
    private val halfTan = sin / (1.0 + cos)

    private val bezier =
        CubicBezier(
            UnitOffset(-extendedFraction.toDouble(), 0.0),
            UnitOffset((1.0 - 1.5 / (1.0 + cos)) * halfTan, 0.0),
            UnitOffset(halfTan, 0.0),
            UnitOffset(sin, 1.0 - cos)
        )

    private var _capsuleBezier: CubicBezier? = null

    private val capsuleBezier
        get() = _capsuleBezier
            ?: bezier.copy(p0 = UnitOffset.Zero).also {
                _capsuleBezier = it
            }

    fun getBezier(extendedFraction: Float): CubicBezier {
        return when (extendedFraction) {
            this.extendedFraction -> bezier
            0f -> capsuleBezier
            else -> G2ContinuityData(circleFraction, extendedFraction).bezier
        }
    }
}
