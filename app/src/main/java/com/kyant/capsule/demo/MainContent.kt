package com.kyant.capsule.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.path.toPath

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
        var showCurvatureComb by remember { mutableStateOf(true) }

        val radiusDp = remember { mutableFloatStateOf(40f) }

        val defaultContinuity = remember { G2Continuity() }
        val extendedFraction = remember { mutableFloatStateOf(defaultContinuity.extendedFraction.toFloat()) }
        val arcFraction = remember { mutableFloatStateOf(defaultContinuity.arcFraction.toFloat()) }
        val bezierCurvatureScale = remember { mutableFloatStateOf(defaultContinuity.bezierCurvatureScale.toFloat()) }
        val arcCurvatureScale = remember { mutableFloatStateOf(defaultContinuity.arcCurvatureScale.toFloat()) }
        val capsuleArcFraction = remember { mutableFloatStateOf(defaultContinuity.capsuleArcFraction.toFloat()) }
        val currentContinuity by remember {
            derivedStateOf {
                G2Continuity(
                    extendedFraction = extendedFraction.floatValue.toDouble(),
                    arcFraction = arcFraction.floatValue.toDouble(),
                    bezierCurvatureScale = bezierCurvatureScale.floatValue.toDouble(),
                    arcCurvatureScale = arcCurvatureScale.floatValue.toDouble(),
                    capsuleArcFraction = capsuleArcFraction.floatValue.toDouble()
                )
            }
        }

        val aspectRatio = remember { mutableFloatStateOf(1.618f) }

        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        var isSvgExportDialogVisible by remember { mutableStateOf(false) }

        Box(
            Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = placeable.width
                    val height = width
                    layout(width, height) {
                        placeable.place(0, 0)
                    }
                }
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
                    .background(Color.Black.copy(alpha = 0.05f))
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
                        val radiusPx =
                            radiusDp.floatValue.dp.toPx().toDouble()
                                .fastCoerceIn(0.0, size.minDimension.toDouble() * 0.5)
                        val pathSegments = currentContinuity.createGeneralRoundedRectanglePathSegments(
                            width = size.width.toDouble(),
                            height = size.height.toDouble(),
                            topLeft = radiusPx,
                            topRight = radiusPx,
                            bottomRight = radiusPx,
                            bottomLeft = radiusPx
                        )

                        if (showCurvatureComb) {
                            val curvatureComb = pathSegments.toCurvatureComb(size.minDimension * 0.15f)
                            drawPath(
                                curvatureComb,
                                Color.Red,
                                style = Stroke(1.dp.toPx())
                            )
                        }

                        if (showBaseline) {
                            drawOutline(
                                RoundedCornerShape(radiusDp.floatValue.dp)
                                    .createOutline(size, layoutDirection, this),
                                color = Color.Red
                            )
                        }

                        drawPath(
                            pathSegments.toPath(),
                            Color(0xFF2196F3)
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
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(end = 64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        .clickable { showCurvatureComb = !showCurvatureComb }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        if (showCurvatureComb) "Hide curvature comb"
                        else "Show curvature comb"
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Slider(
                    aspectRatio,
                    1f..2f,
                    "Aspect ratio",
                    { "%.3f".format(it) },
                )
                Slider(
                    radiusDp,
                    0f..maxRadius.value,
                    "Corner radius",
                    { "${"%.0f".format(it)}dp" },
                )
                Slider(
                    extendedFraction,
                    0f..2f,
                    "Extended fraction",
                    { "%.1f".format(it * 100f) + "%" },
                )
                Slider(
                    arcFraction,
                    0f..1f,
                    "Arc fraction",
                    { "%.1f".format(it * 100f) + "%" },
                )
                Slider(
                    bezierCurvatureScale,
                    0f..3f,
                    "Bezier curvature scale",
                    { "%.2f".format(it) },
                )
                Slider(
                    arcCurvatureScale,
                    0f..3f,
                    "Arc curvature scale",
                    { "%.2f".format(it) },
                )
                Slider(
                    capsuleArcFraction,
                    0f..1f,
                    "Capsule arc fraction",
                    { "%.1f".format(it * 100f) + "%" },
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(Color(0xFF90CAF9))
                        .clickable {
                            scale = 1f
                            offset = Offset.Zero
                            aspectRatio.floatValue = 1.618f
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
                            extendedFraction.floatValue = defaultContinuity.extendedFraction.toFloat()
                            arcFraction.floatValue = defaultContinuity.arcFraction.toFloat()
                            bezierCurvatureScale.floatValue = defaultContinuity.bezierCurvatureScale.toFloat()
                            arcCurvatureScale.floatValue = defaultContinuity.arcCurvatureScale.toFloat()
                            capsuleArcFraction.floatValue = defaultContinuity.capsuleArcFraction.toFloat()
                        }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("Reset parameters")
                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(Color(0xFF90CAF9))
                        .clickable { isSvgExportDialogVisible = true }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("Export SVG")
                }

                if (isSvgExportDialogVisible) {
                    SvgExportDialog(
                        onDismissRequest = { isSvgExportDialogVisible = false },
                        continuity = { currentContinuity }
                    )
                }
            }
        }
    }
}
