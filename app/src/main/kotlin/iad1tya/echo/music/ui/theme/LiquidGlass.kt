/*
 * Echo Music Project Original (2026)
 * Aditya (github.com/iad1tya)
 * Licensed Under GPL-3.0 | see git history for contributors
 * Don't remove this copyright holder!
 */

package iad1tya.echo.music.ui.theme

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// Composition Locals
// ─────────────────────────────────────────────────────────────────────────────

/** Ambient tint color injected by the player (from album art). Defaults to transparent. */
val LocalGlassTint = compositionLocalOf { Color.Transparent }

/** Whether the current surface sits on a dark/blurred background (player expanded). */
val LocalGlassOnDark = compositionLocalOf { false }

// ─────────────────────────────────────────────────────────────────────────────
// Glass Token Constants
// ─────────────────────────────────────────────────────────────────────────────

object LiquidGlassTokens {

    // Base alpha for the frosted fill — lighter feels more glass-like
    const val FillAlphaLight = 0.55f
    const val FillAlphaDark  = 0.35f

    // Tint from album art
    const val TintAlpha = 0.18f

    // Specular highlight (bright rim at the very top)
    const val HighlightAlphaTop    = 0.55f
    const val HighlightAlphaBottom = 0.08f

    // Border
    const val BorderAlphaLight = 0.35f
    const val BorderAlphaDark  = 0.25f
    val BorderWidth            = 0.8.dp

    // Blur radius for the frosted layer
    val BlurRadiusDefault = 24.dp
    val BlurRadiusHeavy   = 36.dp

    // Shape presets
    val ShapeSmall  = RoundedCornerShape(16.dp)
    val ShapeMedium = RoundedCornerShape(24.dp)
    val ShapeLarge  = RoundedCornerShape(32.dp)
    val ShapePill   = RoundedCornerShape(50)
}

// ─────────────────────────────────────────────────────────────────────────────
// Core Modifier
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Applies the Liquid Glass effect to any composable.
 *
 * Layers (bottom → top):
 *  1. Blurred colour fill  → frosted base
 *  2. Semi-transparent tinted overlay
 *  3. Specular gradient drawn over content
 *  4. Hairline border with highlight fade
 */
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassTokens.ShapeMedium,
    tintColor: Color = Color.White,
    blurRadius: Dp = LiquidGlassTokens.BlurRadiusDefault,
    onDark: Boolean = false,
): Modifier {
    val fillAlpha = if (onDark) LiquidGlassTokens.FillAlphaDark
                   else         LiquidGlassTokens.FillAlphaLight

    val borderAlpha = if (onDark) LiquidGlassTokens.BorderAlphaDark
                     else         LiquidGlassTokens.BorderAlphaLight

    // Base tint – blend white and the supplied tint
    val baseFill = Color.White.copy(alpha = fillAlpha)
    val tintedFill = tintColor.copy(alpha = LiquidGlassTokens.TintAlpha)

    val specularBrush = Brush.verticalGradient(
        0.00f to Color.White.copy(alpha = LiquidGlassTokens.HighlightAlphaTop),
        0.25f to Color.White.copy(alpha = LiquidGlassTokens.HighlightAlphaBottom),
        1.00f to Color.Transparent,
    )

    val borderBrush = Brush.verticalGradient(
        0.0f to Color.White.copy(alpha = borderAlpha + 0.20f),
        0.5f to Color.White.copy(alpha = borderAlpha),
        1.0f to Color.White.copy(alpha = borderAlpha * 0.4f),
    )

    return this
        .clip(shape)
        // 1. Frosted base (blur the element's own background rendering)
        .blur(blurRadius, edgeTreatment = BlurredEdgeTreatment(shape))
        // 2. White fill + tint
        .background(baseFill)
        .background(tintedFill)
        // 3. Specular highlight painted over content
        .drawWithContent {
            drawContent()
            drawRect(brush = specularBrush)
        }
        // 4. Gradient border
        .border(
            width  = LiquidGlassTokens.BorderWidth,
            brush  = borderBrush,
            shape  = shape,
        )
}

// ─────────────────────────────────────────────────────────────────────────────
// LiquidGlassSurface — drop-in replacement for Surface/Card
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A ready-made container with the Liquid Glass treatment.
 *
 * Usage:
 * ```
 * LiquidGlassSurface(shape = LiquidGlassTokens.ShapeLarge) {
 *     // your content
 * }
 * ```
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = LiquidGlassTokens.ShapeMedium,
    blurRadius: Dp = LiquidGlassTokens.BlurRadiusDefault,
    tintColor: Color = LocalGlassTint.current,
    onDark: Boolean = LocalGlassOnDark.current,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.liquidGlass(
            shape      = shape,
            tintColor  = tintColor,
            blurRadius = blurRadius,
            onDark     = onDark,
        ),
        content = content,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// LiquidGlassBottomSheet background helper
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Returns the background [Color] to use for a bottom sheet with liquid glass look.
 * The caller still owns clipping / corner rounding.
 *
 * @param progress  Sheet expand progress (0 = collapsed, 1 = fully expanded).
 * @param tint      Optional album-art tint colour.
 * @param darkBase  True when the sheet slides over a dark/blurred background.
 */
fun liquidGlassSheetColor(
    progress: Float,
    tint: Color = Color.Transparent,
    darkBase: Boolean = true,
): Color {
    val fillAlpha = if (darkBase) LiquidGlassTokens.FillAlphaDark
                   else           LiquidGlassTokens.FillAlphaLight
    val base = Color.White.copy(alpha = fillAlpha * progress.coerceIn(0f, 1f))
    if (tint == Color.Transparent) return base
    // blend in the tint
    val t = LiquidGlassTokens.TintAlpha * progress.coerceIn(0f, 1f)
    return base.compositeOver(tint.copy(alpha = t))
}

// ─────────────────────────────────────────────────────────────────────────────
// MiniPlayer glass background helper
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Returns a [Brush] suitable for the MiniPlayer pill container.
 * Uses a subtle diagonal shimmer so the pill looks dimensional.
 */
fun miniPlayerGlassBrush(tint: Color = Color.White): Brush {
    val fill = Color.White.copy(alpha = 0.45f)
    val tinted = if (tint == Color.Transparent || tint == Color.White)
        Color.Transparent
    else
        tint.copy(alpha = 0.12f)

    return Brush.linearGradient(
        0.0f to fill.compositeOver(tinted),
        0.5f to Color.White.copy(alpha = 0.38f).compositeOver(tinted),
        1.0f to Color.White.copy(alpha = 0.28f),
        start = Offset(0f, 0f),
        end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Extension helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Simple alpha-composite: blends [other] on top of [this].
 * Mirrors the CSS `compositeOver` behaviour.
 */
fun Color.compositeOver(other: Color): Color {
    val a = other.alpha
    return Color(
        red   = other.red   * a + this.red   * (1f - a),
        green = other.green * a + this.green * (1f - a),
        blue  = other.blue  * a + this.blue  * (1f - a),
        alpha = this.alpha + other.alpha * (1f - this.alpha),
    )
}

/**
 * Returns a lighter (desaturated) copy of the color, good for glass tints.
 */
fun Color.toGlassTint(alphaMul: Float = 1f): Color {
    val lum = luminance()
    // Boost luminance toward white so the tint reads as glass, not paint
    val boosted = Color(
        red   = (red   + (1f - red)   * 0.45f),
        green = (green + (1f - green) * 0.45f),
        blue  = (blue  + (1f - blue)  * 0.45f),
        alpha = alpha,
    )
    return boosted.copy(alpha = (boosted.alpha * alphaMul).coerceIn(0f, 1f))
}

/** Top-level variant for use in CompositionLocalProvider initializers. */
fun toGlassTint(color: Color, alphaMul: Float = 1f): Color = color.toGlassTint(alphaMul)
