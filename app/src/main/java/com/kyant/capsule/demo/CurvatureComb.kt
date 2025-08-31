package com.kyant.capsule.demo

import androidx.compose.ui.graphics.Path
import com.kyant.capsule.core.Point
import com.kyant.capsule.path.PathSegments
import kotlin.math.atan2

internal fun PathSegments.toCurvatureComb(
    scale: Float = 100f,
    density: Int = 50
): Path {
    val path = Path()
    val maxCurvature = maxOf { segment ->
        (0..density).maxOf { i ->
            val t = i / density.toDouble()
            segment.curvatureAt(t)
        }
    }
    forEach { segment ->
        for (i in 0..density) {
            val t = i / density.toDouble()
            val point = segment.pointAt(t)
            val tangent = segment.unitTangentAt(t)
            val normal = Point(-tangent.y, tangent.x)
            val curvature = segment.curvatureAt(t)
            val combLength = curvature / maxCurvature * scale
            path.moveTo(
                point.x.toFloat(),
                point.y.toFloat()
            )
            path.lineTo(
                (point.x - normal.x * combLength).toFloat(),
                (point.y - normal.y * combLength).toFloat()
            )
        }
    }
    return path
}

internal fun PathSegments.toTangentialAngleComb(
    scale: Float = -300f,
    density: Int = 400
): Path {
    val path = Path()
    var x = 0f
    take(3).forEach { segment ->
        for (i in 0..density) {
            val t = i / density.toDouble()
            val tangent = segment.unitTangentAt(t)
            val angle = atan2(tangent.y, tangent.x)
            val combLength = angle * scale
            path.moveTo(x, 0f)
            path.lineTo(x, combLength.toFloat())
            x += 1f
        }
    }
    return path
}
