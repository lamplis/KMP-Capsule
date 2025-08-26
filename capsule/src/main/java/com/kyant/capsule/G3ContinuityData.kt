package com.kyant.capsule

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal class G3ContinuityData(val extendedFraction: Float) {

    private val rawBezier =
        CubicBezier.generateG2ContinuousBezier(
            start = UnitOffset(-extendedFraction.toDouble(), 0.0),
            end = UnitOffset(sin(Theta), 1f - cos(Theta)),
            startTangentialAngle = 0.0,
            endTangentialAngle = Theta,
            startCurvature = 0.0,
            endCurvature = 1.0
        )

    // split the G2 continuous curve into two segments and adjust the parameters of the split point and end curvature
    // to make it like G3 continuous.
    private val firstBezier = rawBezier.splitFirst(0.6)
    private val midPoint = firstBezier.p3
    private val midTangentialAngleMultiplier = 1.01
    private val midTangentialAngle =
        firstBezier.derivativeAtEnd().let { atan2(it.y, it.x) } * midTangentialAngleMultiplier
    private val d = UnitOffset(1.0, 1.0) * (-0.015 * extendedFraction)
    private val midCurvature = 1.02
    private val endCurvature = 1.06

    private val beziers = listOf(
        CubicBezier.generateG2ContinuousBezier(
            start = rawBezier.p0,
            end = midPoint + d,
            startTangentialAngle = 0.0,
            endTangentialAngle = midTangentialAngle,
            startCurvature = 0.0,
            endCurvature = midCurvature
        ),
        CubicBezier.generateG2ContinuousBezier(
            start = midPoint + d,
            end = rawBezier.p3,
            startTangentialAngle = midTangentialAngle,
            endTangentialAngle = Theta,
            startCurvature = midCurvature,
            endCurvature = endCurvature
        )
    )
    private val beziersReversed = beziers.reversed()

    private var _capsuleBeziers: List<CubicBezier>? = null
    private var _capsuleBeziersReversed: List<CubicBezier>? = null

    private val capsuleBeziers
        get() = _capsuleBeziers
            ?: listOf(
                CubicBezier.generateG2ContinuousBezier(
                    start = UnitOffset.Zero,
                    end = rawBezier.p3,
                    startTangentialAngle = 0.0,
                    endTangentialAngle = Theta,
                    startCurvature = 0.0,
                    endCurvature = 1.0
                )
            ).also {
                _capsuleBeziers = it
            }
    private val capsuleBeziersReversed
        get() = _capsuleBeziersReversed
            ?: capsuleBeziers.reversed().also {
                _capsuleBeziersReversed = it
            }

    fun getBeziers(extendedFraction: Float): List<CubicBezier> {
        return when (extendedFraction) {
            this.extendedFraction -> beziers
            0f -> capsuleBeziers
            else -> G3ContinuityData(extendedFraction).beziers
        }
    }

    fun getBeziersReversed(extendedFraction: Float): List<CubicBezier> {
        return when (extendedFraction) {
            this.extendedFraction -> beziersReversed
            0f -> capsuleBeziersReversed
            else -> G3ContinuityData(extendedFraction).beziersReversed
        }
    }
}

private const val Theta = HalfPI * 0.5
