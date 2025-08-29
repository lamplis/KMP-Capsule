package com.kyant.capsule

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp

@Deprecated(
    "Use G2Continuity instead, G3Continuity is hard to maintain, and employs empirical formulas which may " +
            "not be accurate and scientific.",
)
@Immutable
data class G3Continuity(
    @param:FloatRange(from = 0.0) val extendedFraction: Float = 1f
) : Continuity {

    override val hasSmoothness: Boolean = extendedFraction > 0f

    private val data = G3ContinuityData(extendedFraction)

    override fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        val centerX = width * 0.5
        val centerY = height * 0.5

        val extendedFraction = extendedFraction.toDouble()

        val topLeftFy = ((centerY - topLeft) / topLeft).fastCoerceAtMost(extendedFraction)
        val topLeftFx = ((centerX - topLeft) / topLeft).fastCoerceAtMost(extendedFraction)
        val topRightFx = ((centerX - topRight) / topRight).fastCoerceAtMost(extendedFraction)
        val topRightFy = ((centerY - topRight) / topRight).fastCoerceAtMost(extendedFraction)
        val bottomRightFy = ((centerY - bottomRight) / bottomRight).fastCoerceAtMost(extendedFraction)
        val bottomRightFx = ((centerX - bottomRight) / bottomRight).fastCoerceAtMost(extendedFraction)
        val bottomLeftFx = ((centerX - bottomLeft) / bottomLeft).fastCoerceAtMost(extendedFraction)
        val bottomLeftFy = ((centerY - bottomLeft) / bottomLeft).fastCoerceAtMost(extendedFraction)

        val topLeftDy = -topLeft * topLeftFy
        val topLeftDx = -topLeft * topLeftFx
        val topRightDx = -topRight * topRightFx
        val topRightDy = -topRight * topRightFy
        val bottomRightDy = -bottomRight * bottomRightFy
        val bottomRightDx = -bottomRight * bottomRightFx
        val bottomLeftDx = -bottomLeft * bottomLeftFx
        val bottomLeftDy = -bottomLeft * bottomLeftFy

        val segments = mutableListOf<Segment>()

        // draw clockwise

        var x = 0.0
        var y = topLeft
        var lastPoint = Point(x, y - topLeftDy)

        // top left corner
        if (topLeft > 0f) {
            // π -> 3/4 π
            data.getBeziers(topLeftFy.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x + bezier.p1.y * topLeft, y - bezier.p1.x * topLeft),
                    Point(x + bezier.p2.y * topLeft, y - bezier.p2.x * topLeft),
                    Point(x + bezier.p3.y * topLeft, y - bezier.p3.x * topLeft)
                )
                lastPoint = Point(x + bezier.p3.y * topLeft, y - bezier.p3.x * topLeft)
            }

            // 3/4 π -> 1/2 π
            x = topLeft
            y = 0.0
            data.getBeziersReversed(topLeftFx.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x - bezier.p2.x * topLeft, y + bezier.p2.y * topLeft),
                    Point(x - bezier.p1.x * topLeft, y + bezier.p1.y * topLeft),
                    Point(
                        x - (bezier.p0.x * topLeft).fastCoerceAtLeast(topLeftDx),
                        y + bezier.p0.y * topLeft
                    )
                )
                lastPoint = Point(
                    x - (bezier.p0.x * topLeft).fastCoerceAtLeast(topLeftDx),
                    y + bezier.p0.y * topLeft
                )
            }
        }

        // top line
        x = width - topRight
        y = 0.0
        segments += Segment.Line(
            lastPoint,
            Point(x + topRightDx, y)
        )
        lastPoint = Point(x + topRightDx, y)

        // top right corner
        if (topRight > 0f) {
            // 1/2 π -> 1/4 π
            data.getBeziers(topRightFx.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x + bezier.p1.x * topRight, y + bezier.p1.y * topRight),
                    Point(x + bezier.p2.x * topRight, y + bezier.p2.y * topRight),
                    Point(x + bezier.p3.x * topRight, y + bezier.p3.y * topRight)
                )
                lastPoint = Point(x + bezier.p3.x * topRight, y + bezier.p3.y * topRight)
            }

            // 1/4 π -> 0
            x = width
            y = topRight
            data.getBeziersReversed(topRightFy.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x - bezier.p2.y * topRight, y - bezier.p2.x * topRight),
                    Point(x - bezier.p1.y * topRight, y - bezier.p1.x * topRight),
                    Point(
                        x - bezier.p0.y * topRight,
                        y - (bezier.p0.x * topRight).fastCoerceAtLeast(topRightDy)
                    )
                )
                lastPoint = Point(
                    x - bezier.p0.y * topRight,
                    y - (bezier.p0.x * topRight).fastCoerceAtLeast(topRightDy)
                )
            }
        }

        // right line
        x = width
        y = height - bottomRight
        segments += Segment.Line(
            lastPoint,
            Point(x, y + bottomRightDy)
        )
        lastPoint = Point(x, y + bottomRightDy)

        // bottom right corner
        // 2 π -> 7/4 π
        if (bottomRight > 0f) {
            data.getBeziers(bottomRightFy.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x - bezier.p1.y * bottomRight, y + bezier.p1.x * bottomRight),
                    Point(x - bezier.p2.y * bottomRight, y + bezier.p2.x * bottomRight),
                    Point(x - bezier.p3.y * bottomRight, y + bezier.p3.x * bottomRight)
                )
                lastPoint = Point(x - bezier.p3.y * bottomRight, y + bezier.p3.x * bottomRight)
            }

            // 7/4 π -> 3/2 π
            x = width - bottomRight
            y = height
            data.getBeziersReversed(bottomRightFx.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x + bezier.p2.x * bottomRight, y - bezier.p2.y * bottomRight),
                    Point(x + bezier.p1.x * bottomRight, y - bezier.p1.y * bottomRight),
                    Point(
                        x + (bezier.p0.x * bottomRight).fastCoerceAtLeast(bottomRightDx),
                        y - bezier.p0.y * bottomRight
                    )
                )
                lastPoint = Point(
                    x + (bezier.p0.x * bottomRight).fastCoerceAtLeast(bottomRightDx),
                    y - bezier.p0.y * bottomRight
                )
            }
        }

        // bottom line
        x = bottomLeft
        y = height
        segments += Segment.Line(
            lastPoint,
            Point(x - bottomLeftDx, y)
        )
        lastPoint = Point(x - bottomLeftDx, y)

        // bottom left corner
        if (bottomLeft > 0f) {
            // 3/2 π -> 5/4 π
            data.getBeziers(bottomLeftFx.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x - bezier.p1.x * bottomLeft, y - bezier.p1.y * bottomLeft),
                    Point(x - bezier.p2.x * bottomLeft, y - bezier.p2.y * bottomLeft),
                    Point(x - bezier.p3.x * bottomLeft, y - bezier.p3.y * bottomLeft)
                )
                lastPoint = Point(x - bezier.p3.x * bottomLeft, y - bezier.p3.y * bottomLeft)
            }
            x = 0.0
            y = height - bottomLeft

            // 5/4 π -> π
            data.getBeziersReversed(bottomLeftFy.toFloat()).forEach { bezier ->
                segments += Segment.Cubic(
                    lastPoint,
                    Point(x + bezier.p2.y * bottomLeft, y + bezier.p2.x * bottomLeft),
                    Point(x + bezier.p1.y * bottomLeft, y + bezier.p1.x * bottomLeft),
                    Point(
                        x + bezier.p0.y * bottomLeft,
                        y + (bezier.p0.x * bottomLeft).fastCoerceAtLeast(bottomLeftDy)
                    )
                )
                lastPoint = Point(
                    x + bezier.p0.y * bottomLeft,
                    y + (bezier.p0.x * bottomLeft).fastCoerceAtLeast(bottomLeftDy)
                )
            }
        }

        // left line
        segments += Segment.Line(lastPoint, segments.first().from)

        return segments
    }

    override fun createRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        val (width, height) = size
        val centerX = width * 0.5f
        val centerY = height * 0.5f

        val extendedFraction = extendedFraction

        val topLeftFy = ((centerY - topLeft) / topLeft).fastCoerceAtMost(extendedFraction)
        val topLeftFx = ((centerX - topLeft) / topLeft).fastCoerceAtMost(extendedFraction)
        val topRightFx = ((centerX - topRight) / topRight).fastCoerceAtMost(extendedFraction)
        val topRightFy = ((centerY - topRight) / topRight).fastCoerceAtMost(extendedFraction)
        val bottomRightFy = ((centerY - bottomRight) / bottomRight).fastCoerceAtMost(extendedFraction)
        val bottomRightFx = ((centerX - bottomRight) / bottomRight).fastCoerceAtMost(extendedFraction)
        val bottomLeftFx = ((centerX - bottomLeft) / bottomLeft).fastCoerceAtMost(extendedFraction)
        val bottomLeftFy = ((centerY - bottomLeft) / bottomLeft).fastCoerceAtMost(extendedFraction)

        val topLeftDy = -topLeft * topLeftFy
        val topLeftDx = -topLeft * topLeftFx
        val topRightDx = -topRight * topRightFx
        val topRightDy = -topRight * topRightFy
        val bottomRightDy = -bottomRight * bottomRightFy
        val bottomRightDx = -bottomRight * bottomRightFx
        val bottomLeftDx = -bottomLeft * bottomLeftFx
        val bottomLeftDy = -bottomLeft * bottomLeftFy

        // draw clockwise
        val path = Path().apply {
            var x = 0f
            var y = topLeft
            moveTo(x, y - topLeftDy)

            // top left corner
            if (topLeft > 0f) {
                // π -> 3/4 π
                data.getBeziers(topLeftFy).forEach { bezier ->
                    cubicTo(
                        x + (bezier.p1.y * topLeft).toFloat(),
                        y - (bezier.p1.x * topLeft).toFloat(),
                        x + (bezier.p2.y * topLeft).toFloat(),
                        y - (bezier.p2.x * topLeft).toFloat(),
                        x + (bezier.p3.y * topLeft).toFloat(),
                        y - (bezier.p3.x * topLeft).toFloat()
                    )
                }

                // 3/4 π -> 1/2 π
                x = topLeft
                y = 0f
                data.getBeziersReversed(topLeftFx).forEach { bezier ->
                    cubicTo(
                        x - (bezier.p2.x * topLeft).toFloat(),
                        y + (bezier.p2.y * topLeft).toFloat(),
                        x - (bezier.p1.x * topLeft).toFloat(),
                        y + (bezier.p1.y * topLeft).toFloat(),
                        x - (bezier.p0.x * topLeft).toFloat().fastCoerceAtLeast(topLeftDx),
                        y + (bezier.p0.y * topLeft).toFloat()
                    )
                }
            }

            // top line
            x = width - topRight
            y = 0f
            lineTo(x + topRightDx, y)

            // top right corner
            if (topRight > 0f) {
                // 1/2 π -> 1/4 π
                data.getBeziers(topRightFx).forEach { bezier ->
                    cubicTo(
                        x + (bezier.p1.x * topRight).toFloat(),
                        y + (bezier.p1.y * topRight).toFloat(),
                        x + (bezier.p2.x * topRight).toFloat(),
                        y + (bezier.p2.y * topRight).toFloat(),
                        x + (bezier.p3.x * topRight).toFloat(),
                        y + (bezier.p3.y * topRight).toFloat()
                    )
                }

                // 1/4 π -> 0
                x = width
                y = topRight
                data.getBeziersReversed(topRightFy).forEach { bezier ->
                    cubicTo(
                        x - (bezier.p2.y * topRight).toFloat(),
                        y - (bezier.p2.x * topRight).toFloat(),
                        x - (bezier.p1.y * topRight).toFloat(),
                        y - (bezier.p1.x * topRight).toFloat(),
                        x - (bezier.p0.y * topRight).toFloat(),
                        y - (bezier.p0.x * topRight).toFloat().fastCoerceAtLeast(topRightDy)
                    )
                }
            }

            // right line
            x = width
            y = height - bottomRight
            lineTo(x, y + bottomRightDy)

            // bottom right corner
            // 2 π -> 7/4 π
            if (bottomRight > 0f) {
                data.getBeziers(bottomRightFy).forEach { bezier ->
                    cubicTo(
                        x - (bezier.p1.y * bottomRight).toFloat(),
                        y + (bezier.p1.x * bottomRight).toFloat(),
                        x - (bezier.p2.y * bottomRight).toFloat(),
                        y + (bezier.p2.x * bottomRight).toFloat(),
                        x - (bezier.p3.y * bottomRight).toFloat(),
                        y + (bezier.p3.x * bottomRight).toFloat()
                    )
                }

                // 7/4 π -> 3/2 π
                x = width - bottomRight
                y = height
                data.getBeziersReversed(bottomRightFx).forEach { bezier ->
                    cubicTo(
                        x + (bezier.p2.x * bottomRight).toFloat(),
                        y - (bezier.p2.y * bottomRight).toFloat(),
                        x + (bezier.p1.x * bottomRight).toFloat(),
                        y - (bezier.p1.y * bottomRight).toFloat(),
                        x + (bezier.p0.x * bottomRight).toFloat().fastCoerceAtLeast(bottomRightDx),
                        y - (bezier.p0.y * bottomRight).toFloat()
                    )
                }
            }

            // bottom line
            x = bottomLeft
            y = height
            lineTo(x - bottomLeftDx, y)

            // bottom left corner
            if (bottomLeft > 0f) {
                // 3/2 π -> 5/4 π
                data.getBeziers(bottomLeftFx).forEach { bezier ->
                    cubicTo(
                        x - (bezier.p1.x * bottomLeft).toFloat(),
                        y - (bezier.p1.y * bottomLeft).toFloat(),
                        x - (bezier.p2.x * bottomLeft).toFloat(),
                        y - (bezier.p2.y * bottomLeft).toFloat(),
                        x - (bezier.p3.x * bottomLeft).toFloat(),
                        y - (bezier.p3.y * bottomLeft).toFloat()
                    )
                }
                x = 0f
                y = height - bottomLeft

                // 5/4 π -> π
                data.getBeziersReversed(bottomLeftFy).forEach { bezier ->
                    cubicTo(
                        x + (bezier.p2.y * bottomLeft).toFloat(),
                        y + (bezier.p2.x * bottomLeft).toFloat(),
                        x + (bezier.p1.y * bottomLeft).toFloat(),
                        y + (bezier.p1.x * bottomLeft).toFloat(),
                        x + (bezier.p0.y * bottomLeft).toFloat(),
                        y + (bezier.p0.x * bottomLeft).toFloat().fastCoerceAtLeast(bottomLeftDy)
                    )
                }
            }

            // left line
            close()
        }
        return Outline.Generic(path)
    }

    override fun createHorizontalCapsuleOutline(size: Size): Outline {
        val (width, height) = size
        val centerX = width * 0.5f
        val centerY = height * 0.5f

        val radius = height * 0.5f
        val cornerFx = ((centerX - radius) / radius).fastCoerceAtMost(extendedFraction)
        val cornerDx = -radius * cornerFx

        val beziers = data.getBeziers(cornerFx)
        val beziersReversed = beziers.reversed()

        // draw clockwise
        val path = Path().apply {
            // left circle (5/4 π -> 3/4 π)
            arcTo(
                rect = Rect(Offset(radius, centerY), radius),
                startAngleDegrees = 135f,
                sweepAngleDegrees = 90f,
                forceMoveTo = true
            )

            // top left corner (3/4 π -> 1/2 π)
            var x = radius
            var y = 0f
            beziersReversed.forEach { bezier ->
                cubicTo(
                    x - (bezier.p2.x * radius).toFloat(),
                    y + (bezier.p2.y * radius).toFloat(),
                    x - (bezier.p1.x * radius).toFloat(),
                    y + (bezier.p1.y * radius).toFloat(),
                    x - (bezier.p0.x * radius).toFloat().fastCoerceAtLeast(cornerDx),
                    y + (bezier.p0.y * radius).toFloat()
                )
            }

            // top line
            x = width - radius
            y = 0f
            lineTo(x + cornerDx, y)

            // top right corner (1/2 π -> 1/4 π)
            beziers.forEach { bezier ->
                cubicTo(
                    x + (bezier.p1.x * radius).toFloat(),
                    y + (bezier.p1.y * radius).toFloat(),
                    x + (bezier.p2.x * radius).toFloat(),
                    y + (bezier.p2.y * radius).toFloat(),
                    x + (bezier.p3.x * radius).toFloat(),
                    y + (bezier.p3.y * radius).toFloat()
                )
            }

            // right circle (1/4 π -> -1/4 π)
            arcTo(
                rect = Rect(Offset(width - radius, centerY), radius),
                startAngleDegrees = -45f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // bottom right corner (7/4 π -> 3/2 π)
            x = width - radius
            y = height
            beziersReversed.forEach { bezier ->
                cubicTo(
                    x + (bezier.p2.x * radius).toFloat(),
                    y - (bezier.p2.y * radius).toFloat(),
                    x + (bezier.p1.x * radius).toFloat(),
                    y - (bezier.p1.y * radius).toFloat(),
                    x + (bezier.p0.x * radius).toFloat().fastCoerceAtLeast(cornerDx),
                    y - (bezier.p0.y * radius).toFloat()
                )
            }

            // bottom line
            x = radius
            y = height
            lineTo(x - cornerDx, y)

            // bottom left corner (3/2 π -> 5/4 π)
            beziers.forEach { bezier ->
                cubicTo(
                    x - (bezier.p1.x * radius).toFloat(),
                    y - (bezier.p1.y * radius).toFloat(),
                    x - (bezier.p2.x * radius).toFloat(),
                    y - (bezier.p2.y * radius).toFloat(),
                    x - (bezier.p3.x * radius).toFloat(),
                    y - (bezier.p3.y * radius).toFloat()
                )
            }
        }
        return Outline.Generic(path)
    }

    override fun createVerticalCapsuleOutline(size: Size): Outline {
        val (width, height) = size
        val centerX = width * 0.5f
        val centerY = height * 0.5f

        val radius = width * 0.5f
        val cornerFy = ((centerY - radius) / radius).fastCoerceAtMost(extendedFraction)
        val cornerDy = -radius * cornerFy

        val beziers = data.getBeziers(cornerFy)
        val beziersReversed = beziers.reversed()

        // draw clockwise
        val path = Path().apply {
            var x = 0f
            var y = radius
            moveTo(x, y - cornerDy)

            // top left corner (π -> 3/4 π)
            beziers.forEach { bezier ->
                cubicTo(
                    x + (bezier.p1.y * radius).toFloat(),
                    y - (bezier.p1.x * radius).toFloat(),
                    x + (bezier.p2.y * radius).toFloat(),
                    y - (bezier.p2.x * radius).toFloat(),
                    x + (bezier.p3.y * radius).toFloat(),
                    y - (bezier.p3.x * radius).toFloat()
                )
            }

            // top circle (3/4 π -> 1/4 π)
            arcTo(
                rect = Rect(Offset(centerX, radius), radius),
                startAngleDegrees = -135f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // top right corner (1/4 π -> 0)
            x = width
            y = radius
            beziersReversed.forEach { bezier ->
                cubicTo(
                    x - (bezier.p2.y * radius).toFloat(),
                    y - (bezier.p2.x * radius).toFloat(),
                    x - (bezier.p1.y * radius).toFloat(),
                    y - (bezier.p1.x * radius).toFloat(),
                    x - (bezier.p0.y * radius).toFloat(),
                    y - (bezier.p0.x * radius).toFloat().fastCoerceAtLeast(cornerDy)
                )
            }

            // right line
            x = width
            y = height - radius
            lineTo(x, y + cornerDy)

            // bottom right corner (2 π -> 7/4 π)
            beziers.forEach { bezier ->
                cubicTo(
                    x - (bezier.p1.y * radius).toFloat(),
                    y + (bezier.p1.x * radius).toFloat(),
                    x - (bezier.p2.y * radius).toFloat(),
                    y + (bezier.p2.x * radius).toFloat(),
                    x - (bezier.p3.y * radius).toFloat(),
                    y + (bezier.p3.x * radius).toFloat()
                )
            }

            // bottom circle (7/4 π -> 5/4 π)
            arcTo(
                rect = Rect(Offset(centerX, height - radius), radius),
                startAngleDegrees = 45f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // bottom left corner (5/4 π -> π)
            x = 0f
            y = height - radius
            beziersReversed.forEach { bezier ->
                cubicTo(
                    x + (bezier.p2.y * radius).toFloat(),
                    y + (bezier.p2.x * radius).toFloat(),
                    x + (bezier.p1.y * radius).toFloat(),
                    y + (bezier.p1.x * radius).toFloat(),
                    x + (bezier.p0.y * radius).toFloat(),
                    y + (bezier.p0.x * radius).toFloat().fastCoerceAtLeast(cornerDy)
                )
            }

            // left line
            close()
        }
        return Outline.Generic(path)
    }

    override fun lerp(stop: Continuity, fraction: Float): Continuity {
        return when (stop) {
            is G1Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, 0f, fraction)
                )

            is G2Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, stop.extendedFraction.toFloat(), fraction)
                )

            is G3Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, stop.extendedFraction, fraction)
                )

            else -> stop.lerp(this, 1f - fraction)
        }
    }
}
