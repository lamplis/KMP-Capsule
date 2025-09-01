package com.kyant.capsule.continuities

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceIn
import com.kyant.capsule.AdvancedContinuity
import com.kyant.capsule.Continuity
import com.kyant.capsule.core.CubicBezier
import com.kyant.capsule.core.Point
import com.kyant.capsule.lerp
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.PathSegmentsBuilder
import com.kyant.capsule.path.buildPathSegments
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Immutable
data class G2Continuity(
    @param:FloatRange(from = 0.0, fromInclusive = false) val extendedFraction: Double = 0.5,
    @param:FloatRange(from = 0.0, to = 1.0) val arcFraction: Double = 0.45,
    @param:FloatRange(from = 0.0) val bezierCurvatureScale: Double = 1.10,
    @param:FloatRange(from = 0.0, fromInclusive = false) val arcCurvatureScale: Double = 1.10,
    @param:FloatRange(from = 0.0, to = 1.0) val capsuleArcFraction: Double = arcFraction
) : AdvancedContinuity() {

    private val bezier = generateG2BaseBezier(
        extendedFraction = extendedFraction,
        arcFraction = arcFraction,
        bezierCurvatureScale = bezierCurvatureScale,
        arcCurvatureScale = arcCurvatureScale
    )

    private val capsuleBezier = generateG2BaseBezier(
        extendedFraction = 0.0,
        arcFraction = capsuleArcFraction,
        bezierCurvatureScale = 1.0,
        arcCurvatureScale = 1.0
    )

    private fun getBezier(
        nonCapsuleRatio: Double,
        arcFraction: Double,
        bezierCurvatureScale: Double,
        arcCurvatureScale: Double
    ): CubicBezier {
        if (nonCapsuleRatio == 0.0) {
            return capsuleBezier
        }

        val extendedFraction = this.extendedFraction * nonCapsuleRatio
        if (this.extendedFraction == extendedFraction &&
            this.arcFraction == arcFraction &&
            this.bezierCurvatureScale == bezierCurvatureScale &&
            this.arcCurvatureScale == arcCurvatureScale
        ) {
            return bezier
        }

        return generateG2BaseBezier(
            extendedFraction = extendedFraction,
            arcFraction = arcFraction,
            bezierCurvatureScale = bezierCurvatureScale,
            arcCurvatureScale = arcCurvatureScale
        )
    }

    override fun createStandardRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        val centerX = width * 0.5
        val centerY = height * 0.5

        // mnemonics:
        // T: top, R: right, B: bottom, L: left
        // H: horizontal Bezier, V: vertical Bezier, C: arc, I: line

        // non-capsule ratios of each half corner
        // 0: full capsule, 1: safe rounded rectangle, (0, 1): progressive capsule
        val ratioTLV = ((centerY / topLeft - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTLH = ((centerX / topLeft - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTRH = ((centerX / topRight - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTRV = ((centerY / topRight - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBRV = ((centerY / bottomRight - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBRH = ((centerX / bottomRight - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBLH = ((centerX / bottomLeft - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBLV = ((centerY / bottomLeft - 1.0) / extendedFraction).fastCoerceIn(0.0, 1.0)

        // Bezier stuffs

        // offsets of each half corner
        val offsetTLV = -topLeft * extendedFraction * ratioTLV
        val offsetTLH = -topLeft * extendedFraction * ratioTLH
        val offsetTRH = -topRight * extendedFraction * ratioTRH
        val offsetTRV = -topRight * extendedFraction * ratioTRV
        val offsetBRV = -bottomRight * extendedFraction * ratioBRV
        val offsetBRH = -bottomRight * extendedFraction * ratioBRH
        val offsetBLH = -bottomLeft * extendedFraction * ratioBLH
        val offsetBLV = -bottomLeft * extendedFraction * ratioBLV

        // Bezier curvature scales of each half corner
        val bezKScaleTLV = 1.0 + (bezierCurvatureScale - 1.0) * ratioTLV
        val bezKScaleTLH = 1.0 + (bezierCurvatureScale - 1.0) * ratioTLH
        val bezKScaleTRH = 1.0 + (bezierCurvatureScale - 1.0) * ratioTRH
        val bezKScaleTRV = 1.0 + (bezierCurvatureScale - 1.0) * ratioTRV
        val bezKScaleBRV = 1.0 + (bezierCurvatureScale - 1.0) * ratioBRV
        val bezKScaleBRH = 1.0 + (bezierCurvatureScale - 1.0) * ratioBRH
        val bezKScaleBLH = 1.0 + (bezierCurvatureScale - 1.0) * ratioBLH
        val bezKScaleBLV = 1.0 + (bezierCurvatureScale - 1.0) * ratioBLV

        // arc stuffs

        // constrained non-capsule ratios of each corner
        val ratioTL = min(ratioTLV, ratioTLH)
        val ratioTR = min(ratioTRH, ratioTRV)
        val ratioBR = min(ratioBRV, ratioBRH)
        val ratioBL = min(ratioBLH, ratioBLV)

        // arc fractions of each corner
        val arcFracTL = lerp(capsuleArcFraction, arcFraction, ratioTL)
        val arcFracTR = lerp(capsuleArcFraction, arcFraction, ratioTR)
        val arcFracBR = lerp(capsuleArcFraction, arcFraction, ratioBR)
        val arcFracBL = lerp(capsuleArcFraction, arcFraction, ratioBL)

        // arc curvature scales of each corner
        val arcKScaleTL = 1.0 + (arcCurvatureScale - 1.0) * ratioTL
        val arcKScaleTR = 1.0 + (arcCurvatureScale - 1.0) * ratioTR
        val arcKScaleBR = 1.0 + (arcCurvatureScale - 1.0) * ratioBR
        val arcKScaleBL = 1.0 + (arcCurvatureScale - 1.0) * ratioBL

        // Beziers of each half corner
        val bezierTLV = getBezier(ratioTLV, arcFracTL, bezKScaleTLV, arcKScaleTL)
        val bezierTLH = getBezier(ratioTLH, arcFracTL, bezKScaleTLH, arcKScaleTL)
        val bezierTRH = getBezier(ratioTRH, arcFracTR, bezKScaleTRH, arcKScaleTR)
        val bezierTRV = getBezier(ratioTRV, arcFracTR, bezKScaleTRV, arcKScaleTR)
        val bezierBRV = getBezier(ratioBRV, arcFracBR, bezKScaleBRV, arcKScaleBR)
        val bezierBRH = getBezier(ratioBRH, arcFracBR, bezKScaleBRH, arcKScaleBR)
        val bezierBLH = getBezier(ratioBLH, arcFracBL, bezKScaleBLH, arcKScaleBL)
        val bezierBLV = getBezier(ratioBLV, arcFracBL, bezKScaleBLV, arcKScaleBL)

        return buildPathSegments {
            var x = 0.0
            var y = topLeft
            moveTo(x, y - offsetTLV)

            // TL
            if (topLeft > 0.0) {
                // TLV
                with(bezierTLV) {
                    cubicTo(
                        x + p1.y * topLeft, y - p1.x * topLeft,
                        x + p2.y * topLeft, y - p2.x * topLeft,
                        x + p3.y * topLeft, y - p3.x * topLeft
                    )
                }

                // TLC
                arcToWithScaledRadius(
                    center = Point(topLeft, topLeft),
                    radius = topLeft,
                    radiusScale = 1.0 / arcKScaleTL,
                    startAngle = PI + PI * 0.5 * (1.0 - arcFracTL) * 0.5,
                    sweepAngle = PI * 0.5 * arcFracTL
                )

                // TLH
                x = topLeft
                y = 0.0
                with(bezierTLH) {
                    cubicTo(
                        x - p2.x * topLeft, y + p2.y * topLeft,
                        x - p1.x * topLeft, y + p1.y * topLeft,
                        x - (p0.x * topLeft).fastCoerceAtLeast(offsetTLH), y + p0.y * topLeft
                    )
                }
            }

            // TI
            x = width - topRight
            y = 0.0
            lineTo(x + offsetTRH, y)

            // TR
            if (topRight > 0.0) {
                // TRH
                with(bezierTRH) {
                    cubicTo(
                        x + p1.x * topRight, y + p1.y * topRight,
                        x + p2.x * topRight, y + p2.y * topRight,
                        x + p3.x * topRight, y + p3.y * topRight
                    )
                }

                // TRC
                arcToWithScaledRadius(
                    center = Point(width - topRight, topRight),
                    radius = topRight,
                    radiusScale = 1.0 / arcKScaleTR,
                    startAngle = -PI * 0.5 + PI * 0.5 * (1.0 - arcFracBL) * 0.5,
                    sweepAngle = PI * 0.5 * arcFracTR
                )

                // TRV
                x = width
                y = topRight
                with(bezierTRV) {
                    cubicTo(
                        x - p2.y * topRight, y - p2.x * topRight,
                        x - p1.y * topRight, y - p1.x * topRight,
                        x - p0.y * topRight, y - (p0.x * topRight).fastCoerceAtLeast(offsetTRV)
                    )
                }
            }

            // RI
            x = width
            y = height - bottomRight
            lineTo(x, y + offsetBRV)

            // BR
            if (bottomRight > 0.0) {
                // BRV
                with(bezierBRV) {
                    cubicTo(
                        x - p1.y * bottomRight, y + p1.x * bottomRight,
                        x - p2.y * bottomRight, y + p2.x * bottomRight,
                        x - p3.y * bottomRight, y + p3.x * bottomRight
                    )
                }

                // BRC
                arcToWithScaledRadius(
                    center = Point(width - bottomRight, height - bottomRight),
                    radius = bottomRight,
                    radiusScale = 1.0 / arcKScaleBR,
                    startAngle = 0.0 + PI * 0.5 * (1.0 - arcFracBR) * 0.5,
                    sweepAngle = PI * 0.5 * arcFracBR
                )

                // BRH
                x = width - bottomRight
                y = height
                with(bezierBRH) {
                    cubicTo(
                        x + p2.x * bottomRight, y - p2.y * bottomRight,
                        x + p1.x * bottomRight, y - p1.y * bottomRight,
                        x + (p0.x * bottomRight).fastCoerceAtLeast(offsetBRH), y - p0.y * bottomRight
                    )
                }
            }

            // BI
            x = bottomLeft
            y = height
            lineTo(x - offsetBLH, y)

            // BL
            if (bottomLeft > 0.0) {
                // BLH
                with(bezierBLH) {
                    cubicTo(
                        x - p1.x * bottomLeft, y - p1.y * bottomLeft,
                        x - p2.x * bottomLeft, y - p2.y * bottomLeft,
                        x - p3.x * bottomLeft, y - p3.y * bottomLeft
                    )
                }

                // BLC
                arcToWithScaledRadius(
                    center = Point(bottomLeft, height - bottomLeft),
                    radius = bottomLeft,
                    radiusScale = 1.0 / arcKScaleBL,
                    startAngle = PI * 0.5 + PI * 0.5 * (1.0 - arcFracBL) * 0.5,
                    sweepAngle = PI * 0.5 * arcFracBL
                )

                // BLV
                x = 0.0
                y = height - bottomLeft
                with(bezierBLV) {
                    cubicTo(
                        x + p2.y * bottomLeft, y + p2.x * bottomLeft,
                        x + p1.y * bottomLeft, y + p1.x * bottomLeft,
                        x + p0.y * bottomLeft, y + (p0.x * bottomLeft).fastCoerceAtLeast(offsetBLV)
                    )
                }
            }
        }
    }

    override fun lerp(stop: Continuity, fraction: Double): Continuity {
        return when (stop) {
            is G2Continuity ->
                G2Continuity(
                    extendedFraction = lerp(this.extendedFraction, stop.extendedFraction, fraction),
                    arcFraction = lerp(this.arcFraction, stop.arcFraction, fraction),
                    bezierCurvatureScale = lerp(this.bezierCurvatureScale, stop.bezierCurvatureScale, fraction),
                    arcCurvatureScale = lerp(this.arcCurvatureScale, stop.arcCurvatureScale, fraction),
                    capsuleArcFraction = lerp(this.capsuleArcFraction, stop.capsuleArcFraction, fraction)
                )

            else -> stop.lerp(this, 1f - fraction)
        }
    }
}

private fun generateG2BaseBezier(
    extendedFraction: Double,
    arcFraction: Double,
    bezierCurvatureScale: Double,
    arcCurvatureScale: Double
): CubicBezier {
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
        val lambda0 = -b3 / a2 - b1 * a3 * a3 / a2 / a2 / a2
        CubicBezier(
            Point(-extendedFraction, 0.0),
            Point(-extendedFraction + lambda0, 0.0),
            Point(sin - dy * (endTangent.x / endTangent.y), 0.0),
            Point(sin, 1.0 - cos)
        )
    }
}

private fun PathSegmentsBuilder.arcToWithScaledRadius(
    center: Point,
    radius: Double,
    radiusScale: Double,
    startAngle: Double,
    sweepAngle: Double
) {
    if (radius == 0.0 || sweepAngle == 0.0) {
        return
    }
    if (radiusScale.isInfinite()) {
        val angle = startAngle + sweepAngle
        return lineTo(
            center.x + cos(angle) * radius,
            center.y + sin(angle) * radius
        )
    }
    return if (radiusScale == 1.0) {
        arcTo(
            center = center,
            radius = radius,
            startAngle = startAngle,
            sweepAngle = sweepAngle
        )
    } else {
        val angle = startAngle + sweepAngle
        arcTo(
            x = center.x + cos(angle) * radius,
            y = center.y + sin(angle) * radius,
            radius = radius * radiusScale
        )
    }
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
