package com.kyant.capsule

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp

@Immutable
data class G3Continuity(
    @param:FloatRange(from = 0.0) val extendedFraction: Float = 1f
) : Continuity {

    override val hasSmoothness: Boolean = extendedFraction > 0f

    private val data = G3ContinuityData(extendedFraction)

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

    override fun lerp(stop: Continuity, fraction: Float): Continuity {
        return when (stop) {
            is G1Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, 0f, fraction)
                )

            is G2Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, stop.extendedFraction, fraction)
                )

            is G3Continuity ->
                G3Continuity(
                    extendedFraction = lerp(extendedFraction, stop.extendedFraction, fraction)
                )

            else -> stop.lerp(this, 1f - fraction)
        }
    }
}
