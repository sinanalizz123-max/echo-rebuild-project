package iad1tya.echo.music.ui.component

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
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
 * A modifier that acts like a physical lens, blurring and saturating the content underneath.
 * Optimized for Android 12+ (API 31) using RenderEffect.
 */
fun Modifier.lensModifier(
    radius: Float = 30f,
    saturation: Float = 1.5f,
    alpha: Float = 0.1f,
    shape: Shape = RoundedCornerShape(28.dp)
): Modifier = this.then(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier
            .graphicsLayer {
                val blur = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
                
                // ColorMatrix for Saturation Boost (1.5x)
                val matrix = android.graphics.ColorMatrix().apply {
                    setSaturation(saturation)
                }
                val colorFilter = RenderEffect.createColorFilterEffect(android.graphics.ColorMatrixColorFilter(matrix))
                
                // Chain Blur and Saturation
                renderEffect = RenderEffect.createChainEffect(blur, colorFilter).asComposeRenderEffect()
                
                this.shape = shape
                clip = true
            }
            .background(Color.White.copy(alpha = alpha))
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
    } else {
        // Fallback for older versions
        Modifier
            .blur(radius.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .background(Color.White.copy(alpha = alpha))
            .clip(shape)
    }
)

/**
 * Legacy support for simple glassmorphism
 */
fun Modifier.glassmorphic(
    radius: Dp = 20.dp,
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
    radius: Dp = 30.dp,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = Color.White,
    alpha: Float = 0.1f,
    saturation: Float = 1.5f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .lensModifier(
                radius = radius.value * 2f, // Scaling for visual density
                saturation = saturation,
                alpha = alpha,
                shape = shape
            )
    ) {
        content()
    }
}
