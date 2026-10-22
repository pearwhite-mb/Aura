package io.github.pearwhite.aura

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import io.github.pearwhite.aura.internal.AuraShaderRenderer
import io.github.pearwhite.aura.internal.PaletteFrame
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.time.Duration.Companion.milliseconds

private const val TIME_PERIOD = (20 * PI).toFloat()

@Composable
public fun Aura(
    palette: AuraPalette,
    modifier: Modifier = Modifier,
    style: AuraStyle = AuraDefaults.Style,
    shape: AuraShape = AuraDefaults.Shape,
    animation: AuraAnimation = AuraDefaults.Animation,
    seed: Float = 0f,
    colorTransition: AnimationSpec<Float>? = AuraDefaults.ColorTransition,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
) {
    if (palette.isEmpty) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AuraShader(palette, modifier, style, shape, animation, seed, colorTransition)
    } else if (fallback != null) {
        Box(modifier, content = fallback)
    } else {
        Spacer(modifier.background(palette.toFallbackBrush(filled = shape !is AuraShape.Blob)))
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AuraShader(
    palette: AuraPalette, modifier: Modifier, style: AuraStyle, shape: AuraShape,
    animation: AuraAnimation, seed: Float, colorTransition: AnimationSpec<Float>?,
) {
    val renderer = remember { AuraShaderRenderer() }
    val time = rememberAuraTime(animation)
    val frame = rememberPaletteFrame(palette, colorTransition)
    Spacer(modifier.drawBehind {
        renderer.draw(
            this,
            frame(),
            style,
            shape,
            seed,
            time.floatValue
        )
    })
}

private fun AuraPalette.toFallbackBrush(filled: Boolean): Brush {
    if (filled && colors.size == 1) return SolidColor(colors[0])
    var acc = 0f
    val stops = buildList {
        for (i in colors.indices) {
            add((acc + weights[i] / 2f) * (if (filled) 1f else 0.8f) to colors[i])
            acc += weights[i]
        }
        if (!filled) add(1f to Color.Transparent)
    }.toTypedArray()
    return if (filled) Brush.linearGradient(colorStops = stops)
    else Brush.radialGradient(colorStops = stops)
}

@Composable
private fun rememberPaletteFrame(
    target: AuraPalette,
    spec: AnimationSpec<Float>?,
): () -> PaletteFrame {
    val frame = remember { PaletteFrame() }
    val transition = remember { PaletteTransition(target) }
    val progress = remember { Animatable(1f) }
    val currentSpec by rememberUpdatedState(spec)

    LaunchedEffect(target) {
        if (target == transition.to) return@LaunchedEffect
        transition.from = blend(transition.from, transition.to, progress.value)
        transition.to = target
        val s = currentSpec
        if (s == null) {
            progress.snapTo(1f)
        } else {
            progress.snapTo(0f)
            progress.animateTo(1f, s)
        }
    }
    return {
        blendInto(transition.from, transition.to, progress.value, frame)
        frame
    }
}

private fun mixColor(a: Color, b: Color, f: Float): Color = when {
    f <= 0f -> a
    f >= 1f -> b
    else -> lerp(a, b, f)
}

internal fun blendInto(a: AuraPalette, b: AuraPalette, f: Float, out: PaletteFrame) {
    val n = maxOf(a.size, b.size)
    var cumulative = 0f
    for (i in 0 until n) {
        val c = mixColor(a.colorAt(i), b.colorAt(i), f)
        out.colors[i * 4] = c.red
        out.colors[i * 4 + 1] = c.green
        out.colors[i * 4 + 2] = c.blue
        out.colors[i * 4 + 3] = c.alpha
        cumulative += a.weightAt(i) + (b.weightAt(i) - a.weightAt(i)) * f
        out.bounds[i] = cumulative
    }
    out.count = n
}

internal fun blend(a: AuraPalette, b: AuraPalette, f: Float): AuraPalette {
    val n = maxOf(a.size, b.size)
    return AuraPalette(
        List(n) { mixColor(a.colorAt(it), b.colorAt(it), f) },
        List(n) { a.weightAt(it) + (b.weightAt(it) - a.weightAt(it)) * f },
    )
}

@Composable
private fun rememberAuraTime(animation: AuraAnimation): FloatState {
    val time = remember { mutableFloatStateOf(0f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(animation, lifecycle) {
        if (animation !is AuraAnimation.Flow) return@LaunchedEffect

        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var last = withFrameNanos { it }
            while (true) {
                if (currentCoroutineContext()[MotionDurationScale]?.scaleFactor == 0f) {
                    delay(500.milliseconds)
                    last = withFrameNanos { it }
                    continue
                }
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

private fun AuraPalette.colorAt(i: Int): Color = colors[minOf(i, size - 1)]
private fun AuraPalette.weightAt(i: Int): Float = if (i < size) weights[i] else 0f

private class PaletteTransition(initial: AuraPalette) {
    var from = initial
    var to = initial
}
