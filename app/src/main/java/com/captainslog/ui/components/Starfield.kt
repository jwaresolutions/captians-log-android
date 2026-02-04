package com.captainslog.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Star(
    var x: Float,
    var y: Float,
    var z: Float,
    var prevX: Float? = null,
    var prevY: Float? = null
)

/**
 * Animated starfield background that renders stars moving toward the viewer,
 * creating a warp-speed effect. Ported from the web version's canvas starfield.
 */
@Composable
fun Starfield(
    modifier: Modifier = Modifier,
    numStars: Int = 150,
    speed: Float = 2f,
    opacity: Float = 0.4f
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val stars = remember(numStars) {
        mutableStateListOf<Star>().apply {
            repeat(numStars) {
                add(Star(
                    x = Random.nextFloat() * 2000f - 1000f,
                    y = Random.nextFloat() * 2000f - 1000f,
                    z = Random.nextFloat() * 1000f
                ))
            }
        }
    }

    // Animation tick
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60fps
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            if (w > 0 && h > 0) {
                stars.forEachIndexed { index, star ->
                    star.z -= speed
                    if (star.z <= 0) {
                        star.x = Random.nextFloat() * w * 2 - w
                        star.y = Random.nextFloat() * h * 2 - h
                        star.z = w.coerceAtLeast(1000f)
                        star.prevX = null
                        star.prevY = null
                    }
                }
                tick++
            }
        }
    }

    // Force recomposition on tick
    val currentTick = tick

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
    ) {
        val w = this.size.width
        val h = this.size.height
        if (w <= 0 || h <= 0) return@Canvas

        val centerX = w / 2f
        val centerY = h / 2f
        val maxZ = w.coerceAtLeast(1000f)

        // Use currentTick to prevent dead code elimination
        @Suppress("UNUSED_EXPRESSION")
        currentTick

        stars.forEach { star ->
            if (star.z <= 0) return@forEach
            val k = 128f / star.z
            val px = star.x * k + centerX
            val py = star.y * k + centerY

            if (px in 0f..w && py in 0f..h) {
                val depthRatio = (1f - star.z / maxZ).coerceIn(0f, 1f)
                val starSize = depthRatio * 3f
                val brightness = depthRatio
                val alpha = (0.3f + depthRatio * 0.7f) * opacity

                // Draw trail
                val prevX = star.prevX
                val prevY = star.prevY
                if (prevX != null && prevY != null) {
                    drawLine(
                        color = Color(brightness, brightness, 1f, alpha * 0.4f),
                        start = Offset(prevX, prevY),
                        end = Offset(px, py),
                        strokeWidth = starSize * 0.5f
                    )
                }

                // Draw star
                drawCircle(
                    color = Color(brightness, brightness, 1f, alpha),
                    radius = starSize.coerceAtLeast(0.5f),
                    center = Offset(px, py)
                )

                star.prevX = px
                star.prevY = py
            }
        }
    }
}
