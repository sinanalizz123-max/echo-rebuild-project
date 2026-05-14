package iad1tya.echo.music.ui.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A modifier that applies a glassmorphism style (transparency + border).
 * Note: This does NOT apply blur to the content itself to avoid blurring foreground elements.
 * Use [GlassmorphicContainer] if you need the background blur effect.
 */
fun Modifier.glassmorphic(
    radius: Dp = 20.dp, // Kept for API compatibility, but not used for content blur
    shape: Shape = RoundedCornerShape(16.dp),
    alpha: Float = 0.4f,
    borderAlpha: Float = 0.15f,
    borderColor: Color = Color.White
): Modifier = this
    .clip(shape)
    .background(
        Color.White.copy(alpha = alpha)
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            listOf(
                borderColor.copy(alpha = borderAlpha * 2f),
                borderColor.copy(alpha = borderAlpha)
            )
        ),
        shape = shape
    )

@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    radius: Dp = 20.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = 0.4f,
    borderAlpha: Float = 0.15f,
    borderColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
    ) {
        // Blur Background Layer (Frosted Glass)
        // We put this in a separate Box so it doesn't blur the 'content'
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                radius.toPx(),
                                radius.toPx(),
                                android.graphics.Shader.TileMode.CLAMP
                            ).asComposeRenderEffect()
                        }
                    } else {
                        Modifier.blur(radius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    }
                )
                .background(
                    containerColor.copy(alpha = alpha)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            borderColor.copy(alpha = borderAlpha * 2f),
                            borderColor.copy(alpha = borderAlpha)
                        )
                    ),
                    shape = shape
                )
        )

        // Content Layer (STAYS CRISP)
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
