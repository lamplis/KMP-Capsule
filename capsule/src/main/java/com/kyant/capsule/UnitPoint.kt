package com.kyant.capsule

internal data class UnitPoint(val x: Double, val y: Double) {

    operator fun unaryMinus(): UnitPoint {
        return UnitPoint(-x, -y)
    }

    operator fun minus(other: UnitPoint): UnitPoint {
        return UnitPoint(x - other.x, y - other.y)
    }

    operator fun plus(other: UnitPoint): UnitPoint {
        return UnitPoint(x + other.x, y + other.y)
    }

    operator fun times(operand: Double): UnitPoint {
        return UnitPoint(x * operand, y * operand)
    }

    operator fun div(operand: Double): UnitPoint {
        return UnitPoint(x / operand, y / operand)
    }

    companion object {

        val Zero = UnitPoint(0.0, 0.0)
    }
}

internal fun lerp(start: UnitPoint, stop: UnitPoint, fraction: Double): UnitPoint {
    return UnitPoint(
        lerp(start.x, stop.x, fraction),
        lerp(start.y, stop.y, fraction)
    )
}
