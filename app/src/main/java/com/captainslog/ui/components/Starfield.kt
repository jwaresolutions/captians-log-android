package com.captainslog.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Star(
    var x: Float,
    var y: Float,
    var z: Float
)

/**
 * Animated starfield background that renders stars moving toward the viewer,
 * creating a warp-speed effect with motion-blur trails.
 * Uses a persistent bitmap to achieve the website's trailing fade effect.
 */
@Composable
fun Starfield(
    modifier: Modifier = Modifier,
    numStars: Int = 300,
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

    // Persistent bitmap for motion blur effect
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var androidCanvas by remember { mutableStateOf<AndroidCanvas?>(null) }

    // Recreate bitmap when size changes
    LaunchedEffect(size) {
        if (size.width > 0 && size.height > 0) {
            bitmap?.recycle()
            val newBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
            bitmap = newBitmap
            androidCanvas = AndroidCanvas(newBitmap)
        }
    }

    // Clean up bitmap on dispose
    DisposableEffect(Unit) {
        onDispose {
            bitmap?.recycle()
            bitmap = null
            androidCanvas = null
        }
    }

    // Paints for drawing
    val fadePaint = remember { Paint().apply {
        color = android.graphics.Color.argb(25, 0, 0, 0) // semi-transparent black for trails
    } }
    val starPaint = remember { Paint().apply {
        isAntiAlias = true
    } }

    // Animation tick
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60fps
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val bmp = bitmap
            val cv = androidCanvas
            if (w > 0 && h > 0 && bmp != null && cv != null) {
                val centerX = w / 2f
                val centerY = h / 2f
                val maxZ = w.coerceAtLeast(1000f)

                // Draw fade overlay on persistent bitmap (creates motion blur trails)
                cv.drawRect(0f, 0f, w, h, fadePaint)

                // Update and draw stars onto persistent bitmap
                stars.forEach { star ->
                    star.z -= speed
                    if (star.z <= 0) {
                        star.x = Random.nextFloat() * w * 2 - w
                        star.y = Random.nextFloat() * h * 2 - h
                        star.z = w.coerceAtLeast(1000f) * (0.5f + Random.nextFloat() * 0.5f)
                    }

                    if (star.z > 0) {
                        val k = 128f / star.z
                        val px = star.x * k + centerX
                        val py = star.y * k + centerY

                        if (px in 0f..w && py in 0f..h) {
                            val depthRatio = (1f - star.z / maxZ).coerceIn(0f, 1f)
                            val starSize = (depthRatio * 2f).coerceAtLeast(0.5f)
                            val brightness = (depthRatio * 255).toInt()
                            val alpha = ((0.5f + depthRatio * 0.5f) * opacity * 255).toInt().coerceIn(0, 255)

                            starPaint.color = android.graphics.Color.argb(alpha, brightness, brightness, 255)
                            cv.drawCircle(px, py, starSize, starPaint)
                        }
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
        @Suppress("UNUSED_EXPRESSION")
        currentTick

        val bmp = bitmap
        if (bmp != null && !bmp.isRecycled) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(bmp, 0f, 0f, null)
            }
        }
    }
}
