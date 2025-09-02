package com.kyant.capsule.continuities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceIn
import com.kyant.capsule.AdvancedContinuity
import com.kyant.capsule.Continuity
import com.kyant.capsule.core.Point
import com.kyant.capsule.lerp
import com.kyant.capsule.path.PathSegments
import com.kyant.capsule.path.PathSegmentsBuilder
import com.kyant.capsule.path.buildPathSegments
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Immutable
data class G2Continuity(
    val config: G2ContinuityConfig = G2ContinuityConfig.RoundedRectangle,
    val capsuleConfig: G2ContinuityConfig = G2ContinuityConfig.Capsule
) : AdvancedContinuity() {

    private fun resolveBezier(config: G2ContinuityConfig) =
        when (config) {
            this.config -> this.config.bezier
            this.capsuleConfig -> this.capsuleConfig.bezier
            else -> config.bezier
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
        val ratioTLV = ((centerY / topLeft - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTLH = ((centerX / topLeft - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTRH = ((centerX / topRight - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioTRV = ((centerY / topRight - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBRV = ((centerY / bottomRight - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBRH = ((centerX / bottomRight - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBLH = ((centerX / bottomLeft - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)
        val ratioBLV = ((centerY / bottomLeft - 1.0) / config.extendedFraction).fastCoerceIn(0.0, 1.0)

        // constrained non-capsule ratios of each corner
        val ratioTL = min(ratioTLV, ratioTLH)
        val ratioTR = min(ratioTRH, ratioTRV)
        val ratioBR = min(ratioBRV, ratioBRH)
        val ratioBL = min(ratioBLH, ratioBLV)

        // Bezier stuffs

        // extended fractions of each corner
        val extFracTL = lerp(capsuleConfig.extendedFraction, config.extendedFraction, ratioTL)
        val extFracTR = lerp(capsuleConfig.extendedFraction, config.extendedFraction, ratioTR)
        val extFracBR = lerp(capsuleConfig.extendedFraction, config.extendedFraction, ratioBR)
        val extFracBL = lerp(capsuleConfig.extendedFraction, config.extendedFraction, ratioBL)

        // resolved extended fractions of each half corner
        val extFracTLV = extFracTL * ratioTLV
        val extFracTLH = extFracTL * ratioTLH
        val extFracTRH = extFracTR * ratioTRH
        val extFracTRV = extFracTR * ratioTRV
        val extFracBRV = extFracBR * ratioBRV
        val extFracBRH = extFracBR * ratioBRH
        val extFracBLH = extFracBL * ratioBLH
        val extFracBLV = extFracBL * ratioBLV

        // offsets of each half corner
        val offsetTLV = -topLeft * extFracTLV
        val offsetTLH = -topLeft * extFracTLH
        val offsetTRH = -topRight * extFracTRH
        val offsetTRV = -topRight * extFracTRV
        val offsetBRV = -bottomRight * extFracBRV
        val offsetBRH = -bottomRight * extFracBRH
        val offsetBLH = -bottomLeft * extFracBLH
        val offsetBLV = -bottomLeft * extFracBLV

        // Bezier curvature scales of each half corner
        val bezKScaleTLV = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioTLV)
        val bezKScaleTLH = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioTLH)
        val bezKScaleTRH = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioTRH)
        val bezKScaleTRV = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioTRV)
        val bezKScaleBRV = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioBRV)
        val bezKScaleBRH = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioBRH)
        val bezKScaleBLH = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioBLH)
        val bezKScaleBLV = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioBLV)

        // arc stuffs

        // arc fractions of each corner
        val arcFracTL = lerp(capsuleConfig.arcFraction, config.arcFraction, ratioTL)
        val arcFracTR = lerp(capsuleConfig.arcFraction, config.arcFraction, ratioTR)
        val arcFracBR = lerp(capsuleConfig.arcFraction, config.arcFraction, ratioBR)
        val arcFracBL = lerp(capsuleConfig.arcFraction, config.arcFraction, ratioBL)

        // arc curvature scales of each corner
        val arcKScaleTL = 1.0 + (config.arcCurvatureScale - 1.0) * ratioTL
        val arcKScaleTR = 1.0 + (config.arcCurvatureScale - 1.0) * ratioTR
        val arcKScaleBR = 1.0 + (config.arcCurvatureScale - 1.0) * ratioBR
        val arcKScaleBL = 1.0 + (config.arcCurvatureScale - 1.0) * ratioBL

        // base Beziers of each half corner
        val bezierTLV = resolveBezier(G2ContinuityConfig(extFracTLV, arcFracTL, bezKScaleTLV, arcKScaleTL))
        val bezierTLH = resolveBezier(G2ContinuityConfig(extFracTLH, arcFracTL, bezKScaleTLH, arcKScaleTL))
        val bezierTRH = resolveBezier(G2ContinuityConfig(extFracTRH, arcFracTR, bezKScaleTRH, arcKScaleTR))
        val bezierTRV = resolveBezier(G2ContinuityConfig(extFracTRV, arcFracTR, bezKScaleTRV, arcKScaleTR))
        val bezierBRV = resolveBezier(G2ContinuityConfig(extFracBRV, arcFracBR, bezKScaleBRV, arcKScaleBR))
        val bezierBRH = resolveBezier(G2ContinuityConfig(extFracBRH, arcFracBR, bezKScaleBRH, arcKScaleBR))
        val bezierBLH = resolveBezier(G2ContinuityConfig(extFracBLH, arcFracBL, bezKScaleBLH, arcKScaleBL))
        val bezierBLV = resolveBezier(G2ContinuityConfig(extFracBLV, arcFracBL, bezKScaleBLV, arcKScaleBL))

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

            // LI
            close()
        }
    }

    override fun createHorizontalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val radius = height * 0.5
        val centerX = width * 0.5

        val ratioH = ((centerX / radius - 1.0) / capsuleConfig.extendedFraction).fastCoerceIn(0.0, 1.0)
        val extFrac = capsuleConfig.extendedFraction
        val extFracH = extFrac * ratioH
        val offsetH = -radius * extFracH
        val bezKScaleH = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioH)
        val arcFrac = capsuleConfig.arcFraction
        val bezierH =
            resolveBezier(
                G2ContinuityConfig(
                    extendedFraction = extFracH,
                    arcFraction = arcFrac,
                    bezierCurvatureScale = bezKScaleH,
                    arcCurvatureScale = 1.0
                )
            ) * radius

        val arcRad = PI * 0.5 * arcFrac
        val bezRad = (PI * 0.5 - arcRad) * 0.5
        val sweepRad = (bezRad + arcRad) * 2.0

        return buildPathSegments {
            var x = 0.0
            var y = radius
            moveTo(x, y)

            // LC
            arcTo(
                center = Point(radius, radius),
                radius = radius,
                startAngle = PI * 0.5 + bezRad,
                sweepAngle = sweepRad
            )

            // TLH
            x = radius
            y = 0.0
            with(bezierH) {
                cubicTo(
                    x - p2.x, y + p2.y,
                    x - p1.x, y + p1.y,
                    x - p0.x.fastCoerceAtLeast(offsetH), y + p0.y
                )
            }

            // TI
            x = width - radius
            y = 0.0
            lineTo(x + offsetH, y)

            // TRH
            with(bezierH) {
                cubicTo(
                    x + p1.x, y + p1.y,
                    x + p2.x, y + p2.y,
                    x + p3.x, y + p3.y
                )
            }

            // RC
            arcTo(
                center = Point(width - radius, radius),
                radius = radius,
                startAngle = -(PI * 0.5 - bezRad),
                sweepAngle = sweepRad
            )

            // BRH
            x = width - radius
            y = height
            with(bezierH) {
                cubicTo(
                    x + p2.x, y - p2.y,
                    x + p1.x, y - p1.y,
                    x + p0.x.fastCoerceAtLeast(offsetH), y - p0.y
                )
            }

            // BI
            x = radius
            y = height
            lineTo(x - offsetH, y)

            // BLH
            with(bezierH) {
                cubicTo(
                    x - p1.x, y - p1.y,
                    x - p2.x, y - p2.y,
                    x - p3.x, y - p3.y
                )
            }
        }
    }

    override fun createVerticalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val radius = width * 0.5
        val centerY = height * 0.5

        val ratioV = ((centerY / radius - 1.0) / capsuleConfig.extendedFraction).fastCoerceIn(0.0, 1.0)
        val extFrac = capsuleConfig.extendedFraction
        val extFracV = extFrac * ratioV
        val offsetV = -radius * extFracV
        val bezKScaleV = lerp(capsuleConfig.bezierCurvatureScale, config.bezierCurvatureScale, ratioV)
        val arcFrac = capsuleConfig.arcFraction
        val bezierV =
            resolveBezier(
                G2ContinuityConfig(
                    extendedFraction = extFracV,
                    arcFraction = arcFrac,
                    bezierCurvatureScale = bezKScaleV,
                    arcCurvatureScale = 1.0
                )
            ) * radius

        val arcRad = PI * 0.5 * arcFrac
        val bezRad = (PI * 0.5 - arcRad) * 0.5
        val sweepRad = (bezRad + arcRad) * 2.0

        return buildPathSegments {
            var x = 0.0
            var y = radius
            moveTo(x, y - offsetV)

            // TLV
            with(bezierV) {
                cubicTo(
                    x + p1.y, y - p1.x,
                    x + p2.y, y - p2.x,
                    x + p3.y, y - p3.x
                )
            }

            // TC
            arcTo(
                center = Point(radius, radius),
                radius = radius,
                startAngle = -(PI - bezRad),
                sweepAngle = sweepRad
            )

            // TRV
            x = width
            y = radius
            with(bezierV) {
                cubicTo(
                    x - p2.y, y - p2.x,
                    x - p1.y, y - p1.x,
                    x - p0.y, y - p0.x.fastCoerceAtLeast(offsetV)
                )
            }

            // RI
            x = width
            y = height - radius
            lineTo(x, y + offsetV)

            // BRV
            with(bezierV) {
                cubicTo(
                    x - p1.y, y + p1.x,
                    x - p2.y, y + p2.x,
                    x - p3.y, y + p3.x
                )
            }

            // BC
            arcTo(
                center = Point(width - radius, height - radius),
                radius = radius,
                startAngle = bezRad,
                sweepAngle = sweepRad
            )

            // BLV
            x = 0.0
            y = height - radius
            with(bezierV) {
                cubicTo(
                    x + p2.y, y + p2.x,
                    x + p1.y, y + p1.x,
                    x + p0.y, y + p0.x.fastCoerceAtLeast(offsetV)
                )
            }

            // LI
            close()
        }
    }

    override fun lerp(stop: Continuity, fraction: Double): Continuity {
        return when (stop) {
            is G2Continuity ->
                G2Continuity(
                    config = lerp(this.config, stop.config, fraction),
                    capsuleConfig = lerp(this.capsuleConfig, stop.capsuleConfig, fraction)
                )

            else -> stop.lerp(this, 1f - fraction)
        }
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

    if (radiusScale == 1.0) {
        return arcTo(
            center = center,
            radius = radius,
            startAngle = startAngle,
            sweepAngle = sweepAngle
        )
    }

    val angle = startAngle + sweepAngle
    return arcTo(
        x = center.x + cos(angle) * radius,
        y = center.y + sin(angle) * radius,
        radius = radius * radiusScale
    )
}
