package com.kyant.capsule

internal data class UnitOffset(
    val x: Double,
    val y: Double
) {

    operator fun unaryMinus(): UnitOffset {
        return UnitOffset(-x, -y)
    }

    operator fun minus(other: UnitOffset): UnitOffset {
        return UnitOffset(x - other.x, y - other.y)
    }

    operator fun plus(other: UnitOffset): UnitOffset {
        return UnitOffset(x + other.x, y + other.y)
    }

    operator fun times(operand: Double): UnitOffset {
        return UnitOffset(x * operand, y * operand)
    }

    operator fun div(operand: Double): UnitOffset {
        return UnitOffset(x / operand, y / operand)
    }

    companion object {

        val Zero = UnitOffset(0.0, 0.0)
    }
}

internal fun lerp(start: UnitOffset, stop: UnitOffset, fraction: Double): UnitOffset {
    return UnitOffset(
        lerp(start.x, stop.x, fraction),
        lerp(start.y, stop.y, fraction)
    )
}
