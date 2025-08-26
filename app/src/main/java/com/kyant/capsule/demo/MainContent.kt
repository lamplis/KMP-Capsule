package com.kyant.capsule.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.kyant.capsule.G1Continuity
import com.kyant.capsule.G2Continuity
import com.kyant.capsule.G3Continuity

@Composable
fun MainContent() {
    Column(
        Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val maxRadius = with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp() / 2f - 16.dp
        }

        var showBaseline by remember { mutableStateOf(false) }

        val radiusDp = remember { mutableFloatStateOf(40f) }
        val circleFraction = remember { mutableFloatStateOf(0.25f) }
        val extendedFraction = remember { mutableFloatStateOf(1f) }

        val aspectRatio = remember { mutableFloatStateOf(1.618f) }

        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        var currentContinuity by remember { mutableIntStateOf(3) }

        Box(
            Modifier
                .clip(ContinuousCapsule)
                .background(Color(0xFF90CAF9))
                .clickable {
                    currentContinuity =
                        when (currentContinuity) {
                            1 -> 2
                            2 -> 3
                            else -> 1
                        }
                }
                .height(40.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText("Current continuity: G$currentContinuity")
        }

        Box(
            Modifier
                .wrapContentHeight(Alignment.Top)
                .weight(1f)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            scale = (scale * zoom).fastCoerceAtLeast(1f)
                            offset += pan
                        }
                    }
                    .background(Color.LightGray)
                    .clipToBounds()
                    .aspectRatio(1f)
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawBehind {
                        if (showBaseline) {
                            drawOutline(
                                RoundedCornerShape(radiusDp.floatValue.dp)
                                    .createOutline(size, layoutDirection, this),
                                color = Color.Red
                            )
                        }

                        val continuity = when (currentContinuity) {
                            2 -> G2Continuity(
                                circleFraction = circleFraction.floatValue,
                                extendedFraction = extendedFraction.floatValue
                            )

                            3 -> G3Continuity(
                                extendedFraction = extendedFraction.floatValue
                            )

                            else -> G1Continuity
                        }
                        drawOutline(
                            ContinuousRoundedRectangle(
                                radiusDp.floatValue.dp,
                                continuity = continuity
                            ).createOutline(size, layoutDirection, this),
                            color = Color(0xFF2196F3)
                        )
                    }
                    .layout { measurable, constraints ->
                        val width = constraints.maxWidth
                        val height = (width / aspectRatio.floatValue).fastRoundToInt()
                        val placeable = measurable.measure(Constraints.fixed(width, height))

                        layout(width, height) {
                            placeable.place(0, 0)
                        }
                    }
                    .fillMaxWidth()
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Slider(
                radiusDp,
                0f..maxRadius.value,
                "Corner radius",
                { "${"%.0f".format(it)}dp" },
            )
            if (currentContinuity == 2) {
                Slider(
                    circleFraction,
                    0f..1f,
                    "Circle fraction",
                    { "%.1f".format(it * 100f) + "%" },
                )
            }
            if (currentContinuity == 2 || currentContinuity == 3) {
                Slider(
                    extendedFraction,
                    0f..2f,
                    "Extended fraction",
                    { "%.1f".format(it * 100f) + "%" },
                )
            }
            Slider(
                aspectRatio,
                1f..2f,
                "Aspect ratio",
                { "%.3f".format(it) },
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .clip(ContinuousCapsule)
                    .background(Color(0xFF90CAF9))
                    .clickable { showBaseline = !showBaseline }
                    .height(40.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    if (showBaseline) "Hide baseline"
                    else "Show baseline"
                )
            }

            Box(
                Modifier
                    .clip(ContinuousCapsule)
                    .background(Color(0xFF90CAF9))
                    .clickable {
                        scale = 1f
                        offset = Offset.Zero
                    }
                    .height(40.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText("Reset scale & pos")
            }

            Box(
                Modifier
                    .clip(ContinuousCapsule)
                    .background(Color(0xFF90CAF9))
                    .clickable {
                        radiusDp.floatValue = 40f
                        circleFraction.floatValue = 0.25f
                        extendedFraction.floatValue = 1f
                        aspectRatio.floatValue = 1.618f
                        scale = 1f
                        offset = Offset.Zero
                    }
                    .height(40.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText("Reset")
            }
        }
    }
}
