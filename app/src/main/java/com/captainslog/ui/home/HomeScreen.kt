package com.captainslog.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captainslog.R
import com.captainslog.ui.components.Starfield

/**
 * Home screen with animated starfield background and pulsing logo glow effect.
 * Serves as the main landing page of the application.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNotesClick: () -> Unit = {},
    onTodosClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    // Pulsing glow animation - matches web's 3s ease-in-out infinite
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    // Glow size pulse: blur radius grows and shrinks (like CSS drop-shadow)
    val glowSize by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowSize"
    )

    // Fade-in animation
    val fadeIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeIn.animateTo(1f, animationSpec = tween(1000))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Starfield background
        Starfield(
            modifier = Modifier.fillMaxSize(),
            numStars = 300,
            speed = 4f,
            opacity = 0.4f
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(fadeIn.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing logo with orange glow (matching web's drop-shadow)
            val glowColor = Color(0xFFFF9933)
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Orange glow behind logo — solid center fades outward
                // Size and intensity pulse together like CSS drop-shadow
                Canvas(
                    modifier = Modifier.size(400.dp)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val radius = size.minDimension * glowSize
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = glowAlpha * 0.8f),
                                glowColor.copy(alpha = glowAlpha * 0.5f),
                                glowColor.copy(alpha = glowAlpha * 0.2f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = radius
                        ),
                        center = Offset(cx, cy),
                        radius = radius
                    )
                }
                // Actual logo on top — covers glow center
                Image(
                    painter = painterResource(id = R.drawable.captains_log_logo),
                    contentDescription = "Captain's Log",
                    modifier = Modifier
                        .height(200.dp)
                        .alpha(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App subtitle with glow styling
            Text(
                text = "Captain's Log System",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFCC99),
                    shadow = Shadow(
                        color = Color(0xFFFF9933).copy(alpha = 0.5f),
                        offset = Offset.Zero,
                        blurRadius = 10f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Navigate using the tabs below to manage your boats, trips, and more.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF99CCFF).copy(alpha = 0.8f)
            )
        }
    }
}
