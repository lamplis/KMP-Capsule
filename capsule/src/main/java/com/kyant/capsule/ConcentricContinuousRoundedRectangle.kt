package com.kyant.capsule

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Stable
fun ContinuousRoundedRectangle.concentric(innerPadding: Dp): ContinuousRoundedRectangle {
    return ConcentricContinuousRoundedRectangle(this, innerPadding)
}

@Immutable
private data class ConcentricContinuousRoundedRectangle(
    val outerShape: ContinuousRoundedRectangle,
    val innerPadding: Dp
) : ContinuousRoundedRectangle(
    topStart = ConcentricCornerSize(outerShape.topStart, innerPadding),
    topEnd = ConcentricCornerSize(outerShape.topEnd, innerPadding),
    bottomEnd = ConcentricCornerSize(outerShape.bottomEnd, innerPadding),
    bottomStart = ConcentricCornerSize(outerShape.bottomStart, innerPadding),
    continuity = outerShape.continuity
)

@Immutable
private data class ConcentricCornerSize(
    private val outerCornerSize: CornerSize,
    private val innerPadding: Dp
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float {
        return outerCornerSize.toPx(shapeSize, density) - with(density) { innerPadding.toPx() }
    }
}
