package io.github.pearwhite.aura

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/*
 * Reusable buffers that are handed to the shader. Filled on every frame, so it must not allocate.
 */
internal class PaletteFrame {
    /* RGBA of every color, 4 floats each. */
    val colors = FloatArray(AuraDefaults.MaxColors * 4)
    /* Cumulative weight boundaries: bounds[i] = sum of weights 0..i. */
    val bounds = FloatArray(AuraDefaults.MaxColors)
    var count = 0
}

/* Padding rule: a shorter palette is extended with copies of its LAST color at weight 0, so a new
 * color grows from nothing and a removed one shrinks to nothing without a visible trace.
 */
private fun AuraPalette.colorAt(i: Int): Color = colors[minOf(i, size - 1)]
private fun AuraPalette.weightAt(i: Int): Float = if (i < size) weights[i] else 0f

/* Shortcuts for 0 and 1 skip the color-space conversion that `lerp` performs, which is wasted
 * work in the steady state (no transition running).
 */
private fun mixColor(a: Color, b: Color, f: Float): Color = when {
    f <= 0f -> a
    f >= 1f -> b
    else -> lerp(a, b, f)
}

/**
 * Writes the blend of two palettes at [f] (0..1) into [out]. Called every frame in the draw phase,
 * so it does not allocate. Entries are matched by index.
 */
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

/**
 * Same blend, but as a new palette (allocates). Used rarely: to take a snapshot when a transition
 * is interrupted by a new target.
 */
internal fun blend(a: AuraPalette, b: AuraPalette, f: Float): AuraPalette {
    val n = maxOf(a.size, b.size)
    return AuraPalette(
        List(n) { mixColor(a.colorAt(it), b.colorAt(it), f) },
        List(n) { a.weightAt(it) + (b.weightAt(it) - a.weightAt(it)) * f },
    )
}
