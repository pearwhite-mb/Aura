package io.github.pearwhite.aura

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.github.pearwhite.aura.internal.AuraShaderRender
import kotlin.math.PI

private const val TIME_PERIOD = (20 * PI).toFloat()

@Composable
public fun Aura(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    style: AuraStyle = AuraDefaults.Style,
    animation: AuraAnimation = AuraDefaults.Animation,
    seed: Float = 0f,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
) {
    if (colors.isEmpty()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val time = rememberAuraTime(animation)
        Spacer(
            modifier = modifier.drawWithCache {
                val renderer = AuraShaderRender()
                onDrawBehind {
                    renderer.draw(this, colors, style, seed, time.floatValue)
                }
            }
        )
    } else if (fallback != null) {
        Box(modifier, content = fallback)
    } else {
        Spacer(modifier = modifier.background(Brush.radialGradient(colors + Color.Transparent)))
    }
}

@Composable
private fun rememberAuraTime(animation: AuraAnimation): FloatState {
    val time = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(animation) {
        if (animation is AuraAnimation.Flow) {
            var last = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    val dt = (now - last) / 1_000_000_000f
                    last = now
                    time.floatValue = (time.floatValue + dt * animation.speed) % TIME_PERIOD
                }
            }
        }
    }
    return time
}
