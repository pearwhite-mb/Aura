package io.github.pearwhite.aura

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.pearwhite.aura.internal.AuraShaderRenderer
import kotlin.math.PI

private const val TIME_PERIOD = (20 * PI).toFloat()

@Composable
public fun Aura(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    style: AuraStyle = AuraDefaults.Style,
    animation: AuraAnimation = AuraDefaults.Animation,
    seed: Float = 0f,
    colorTransition: AnimationSpec<Float>? = AuraDefaults.ColorTransition,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
) {
    if (colors.isEmpty()) return

    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
    }*/

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AuraShader(colors, modifier, style, animation, seed, colorTransition)
    } else if (fallback != null) {
        Box(modifier, content = fallback)
    } else {
        Spacer(modifier.background(Brush.radialGradient(colors + Color.Transparent)))
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AuraShader(
    colors: List<Color>,
    modifier: Modifier,
    style: AuraStyle,
    animation: AuraAnimation,
    seed: Float,
    colorTransition: AnimationSpec<Float>?,
) {
    val renderer = remember { AuraShaderRenderer() }
    val time = rememberAuraTime(animation)
    val palette = rememberAnimatedPalette(colors, colorTransition)

    Spacer(
        modifier.drawBehind {
            renderer.draw(this, palette(), style, seed, time.floatValue)
        }
    )
}

private class PaletteTransition(initial: List<Color>) {
    var from: List<Color> = initial
    var to: List<Color> = initial

    fun at(fraction: Float): List<Color> {
        if (fraction >= 1f || from === to) return to
        val n = maxOf(from.size, to.size)
        val a = from.resampled(n)
        val b = to.resampled(n)
        return List(n) { lerp(a[it], b[it], fraction) }
    }
}

private fun List<Color>.resampled(n: Int): List<Color> {
    if (size == n) return this
    if (size == 1) return List(n) { this[0] }
    return List(n) { i ->
        val x = i.toFloat() / (n - 1) * (size - 1)
        val lo = x.toInt().coerceAtMost(size - 2)
        lerp(this[lo], this[lo + 1], x - lo)
    }
}


@Composable
private fun rememberAnimatedPalette(
    target: List<Color>,
    spec: AnimationSpec<Float>?,
): () -> List<Color> {
    val transition = remember { PaletteTransition(target) }
    val progress = remember { Animatable(1f) }

    LaunchedEffect(target, spec) {
        if (target == transition.to) return@LaunchedEffect
        if (spec == null) {
            transition.from = target
            transition.to = target
            progress.snapTo(1f)
        } else {
            transition.from = transition.at(progress.value)
            transition.to = target
            progress.snapTo(0f)
            progress.animateTo(1f, spec)
        }
    }
    return { transition.at(progress.value) }
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
