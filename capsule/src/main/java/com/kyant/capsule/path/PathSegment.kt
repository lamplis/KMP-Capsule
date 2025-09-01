package com.kyant.capsule.path

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import com.kyant.capsule.core.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed interface PathSegment {

    val from: Point
    val to: Point

    fun pointAt(t: Double): Point
    fun unitTangentAt(t: Double): Point
    fun curvatureAt(t: Double): Double

    fun drawTo(path: Path)

    data class Line(
        override val from: Point,
        override val to: Point
    ) : PathSegment {

        override fun pointAt(t: Double): Point {
            return from + (to - from) * t
        }

        override fun unitTangentAt(t: Double): Point {
            return (to - from).normalized()
        }

        override fun curvatureAt(t: Double): Double {
            return 0.0
        }

        override fun drawTo(path: Path) {
            path.lineTo(to.x.toFloat(), to.y.toFloat())
        }
    }

    data class Arc(
        val center: Point,
        val radius: Double,
        val startAngle: Double,
        val sweepAngle: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(
                center.x + cos(startAngle) * radius,
                center.y + sin(startAngle) * radius
            )

        override val to: Point
            get() = Point(
                center.x + cos(startAngle + sweepAngle) * radius,
                center.y + sin(startAngle + sweepAngle) * radius
            )

        override fun pointAt(t: Double): Point {
            val angle = startAngle + sweepAngle * t
            return Point(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius
            )
        }

        override fun unitTangentAt(t: Double): Point {
            val angle = startAngle + sweepAngle * t
            return Point(-sin(angle), cos(angle))
        }

        override fun curvatureAt(t: Double): Double {
            return 1.0 / radius
        }

        override fun drawTo(path: Path) {
            path.arcToRad(
                rect = Rect(
                    center = Offset(center.x.toFloat(), center.y.toFloat()),
                    radius = radius.toFloat()
                ),
                startAngleRadians = startAngle.toFloat(),
                sweepAngleRadians = sweepAngle.toFloat(),
                forceMoveTo = false
            )
        }
    }

    data class Circle(
        val center: Point,
        val radius: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(center.x + radius, center.y)

        override val to: Point
            get() = from

        override fun pointAt(t: Double): Point {
            val angle = 2.0 * PI * t
            return Point(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius
            )
        }

        override fun unitTangentAt(t: Double): Point {
            val angle = 2.0 * PI * t
            return Point(-sin(angle), cos(angle))
        }

        override fun curvatureAt(t: Double): Double {
            return 1.0 / radius
        }

        override fun drawTo(path: Path) {
            path.asAndroidPath().addCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                radius.toFloat(),
                android.graphics.Path.Direction.CW
            )
        }
    }

    data class Cubic(
        val p0: Point,
        val p1: Point,
        val p2: Point,
        val p3: Point
    ) : PathSegment {

        override val from: Point
            get() = p0

        override val to: Point
            get() = p3

        override fun pointAt(t: Double): Point {
            val u = 1.0 - t
            return p0 * (u * u * u) + p1 * (3.0 * u * u * t) + p2 * (3.0 * u * t * t) + p3 * (t * t * t)
        }

        override fun unitTangentAt(t: Double): Point {
            val u = 1.0 - t
            return ((p1 - p0) * (3.0 * u * u) + (p2 - p1) * (6.0 * u * t) + (p3 - p2) * (3.0 * t * t)).normalized()
        }

        override fun curvatureAt(t: Double): Double {
            val u = 1.0 - t
            val d1 = (p1 - p0) * (3.0 * u * u) + (p2 - p1) * (6.0 * u * t) + (p3 - p2) * (3.0 * t * t)
            val d2 = (p2 - p1 * 2.0 + p0) * (6.0 * (1.0 - t)) + (p3 - p2 * 2.0 + p1) * (6.0 * t)
            val cross = d1.x * d2.y - d1.y * d2.x
            val len = sqrt(d1.x * d1.x + d1.y * d1.y)
            return if (len != 0.0) {
                cross / (len * len * len)
            } else {
                0.0
            }
        }

        override fun drawTo(path: Path) {
            path.cubicTo(
                p1.x.toFloat(), p1.y.toFloat(),
                p2.x.toFloat(), p2.y.toFloat(),
                p3.x.toFloat(), p3.y.toFloat()
            )
        }
    }
}
