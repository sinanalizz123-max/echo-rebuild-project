package iad1tya.echo.music.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * A background component that renders animated "water drops" or "blobs"
 * to achieve a watermorphism effect.
 */
@Composable
fun WaterBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    val dropColor = if (isDark) Color(0xFF0077CC) else Color(0xFF33AAFF)
    
    Box(modifier = modifier.fillMaxSize()) {
        // Base watery gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            dropColor.copy(alpha = 0.15f),
                            Color.Transparent,
                            dropColor.copy(alpha = 0.1f)
                        )
                    )
                )
        )

        // Animated Water Drops
        val infiniteTransition = rememberInfiniteTransition(label = "WaterDrops")
        
        repeat(6) { index ->
            val randomX = remember { Random.nextFloat() }
            val randomY = remember { Random.nextFloat() }
            val duration = remember { Random.nextInt(15000, 30000) }
            val delay = remember { Random.nextInt(0, 5000) }
            val size = remember { Random.nextInt(150, 400).dp }

            val xOffset by infiniteTransition.animateFloat(
                initialValue = -50f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dropX_$index"
            )
            
            val yOffset by infiniteTransition.animateFloat(
                initialValue = -50f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration + 2000, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dropY_$index"
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.3f)
                    .blur(60.dp)
            ) {
                drawCircle(
                    color = dropColor.copy(alpha = 0.4f),
                    radius = size.toPx(),
                    center = Offset(
                        x = (randomX * size.width) + xOffset.dp.toPx(),
                        y = (randomY * size.height) + yOffset.dp.toPx()
                    )
                )
            }
        }
    }
}
