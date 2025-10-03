package com.kyant.capsule.path

import com.kyant.capsule.core.Point

inline fun buildPathSegments(block: PathSegmentsBuilder.() -> Unit): PathSegments {
    return PathSegmentsBuilder().apply(block).build()
}

fun buildCirclePathSegments(center: Point, radius: Double): PathSegments {
    return listOf(PathSegment.Circle(center, radius))
}

class PathSegmentsBuilder {

    private var startPoint = Point.Zero
    private var currentPoint = Point.Zero
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
