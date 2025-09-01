package com.kyant.capsule.path

import com.kyant.capsule.core.Point
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

inline fun buildPathSegments(block: PathSegmentsBuilder.() -> Unit): PathSegments {
    return PathSegmentsBuilder().apply(block).build()
}

fun buildCirclePathSegments(center: Point, radius: Double): PathSegments {
    return listOf(PathSegment.Circle(center, radius))
}

class PathSegmentsBuilder {

    private var startPoint = Point.Companion.Zero
    private var currentPoint = Point.Companion.Zero
    private var didMove = false

    private var segments = mutableListOf<PathSegment>()

    fun moveTo(x: Double, y: Double) {
        if (didMove) {
            throw IllegalStateException("moveTo can only be called once at the beginning of the path")
        }
        didMove = true
        startPoint = Point(x, y)
        currentPoint = startPoint
    }

    fun lineTo(x: Double, y: Double) {
        val segment = PathSegment.Line(currentPoint, Point(x, y))
        segments += segment
        currentPoint = segment.to
    }

    fun arcTo(center: Point, radius: Double, startAngle: Double, sweepAngle: Double) {
        val segment = PathSegment.Arc(center, radius, startAngle, sweepAngle)
        segments += segment
        currentPoint = segment.to
    }

    fun arcTo(x: Double, y: Double, radius: Double) {
        val from = currentPoint
        val to = Point(x, y)
        val mid = (from + to) * 0.5
        val dir = to - from
        val len = sqrt(dir.x * dir.x + dir.y * dir.y)
        val height = sqrt(radius * radius - (len * 0.5) * (len * 0.5))
        val norm = Point(-dir.y / len, dir.x / len)
        val center = mid + norm * height * if (radius > 0) 1.0 else -1.0
        val startAngle = atan2(from.y - center.y, from.x - center.x)
        val endAngle = atan2(to.y - center.y, to.x - center.x)
        val sweepAngle = when {
            radius > 0 && endAngle >= startAngle -> endAngle - startAngle
            radius > 0 && endAngle < startAngle -> endAngle + 2.0 * PI - startAngle
            radius < 0 && endAngle <= startAngle -> endAngle - startAngle
            radius < 0 && endAngle > startAngle -> endAngle - 2.0 * PI - startAngle
            else -> 0.0
        }
        arcTo(center, radius, startAngle, sweepAngle)
    }

    fun cubicTo(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) {
        val segment = PathSegment.Cubic(
            currentPoint,
            Point(x1, y1),
            Point(x2, y2),
            Point(x3, y3)
        )
        segments += segment
        currentPoint = segment.to
    }

    fun close() {
        val segment = PathSegment.Line(currentPoint, startPoint)
        segments += segment
        currentPoint = segment.to
    }

    fun build(): PathSegments {
        return segments.toList()
    }
}
