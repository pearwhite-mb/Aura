package io.github.pearwhitemb.aura.internal

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.pearwhitemb.aura.AuraPatternFit
import io.github.pearwhitemb.aura.AuraShape
import io.github.pearwhitemb.aura.AuraStyle
import io.github.pearwhitemb.aura.PaletteFrame

/**
 * Owns the RuntimeShader and feeds it with uniforms. Android 13+ only.
 * Create once and call [draw] from the draw phase only (not thread-safe).
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class AuraShaderRenderer {
    /* Invalid AGSL throws right here, at construction time, not on the first draw.*/
    private val shader = RuntimeShader(AURA_SHADER_SRC)
    private val brush = ShaderBrush(shader)

    fun draw(
        scope: DrawScope,
        frame: PaletteFrame,
        style: AuraStyle,
        shape: AuraShape,
        seed: Float,
        time: Float,
    ) {
        /* Never let the size be 0: the shader divides by it. */
        val w = scope.size.width.coerceAtLeast(1f)
        val h = scope.size.height.coerceAtLeast(1f)

        shader.setFloatUniform("resolution", scope.size.width, scope.size.height)
        shader.setFloatUniform("colors", frame.colors)
        shader.setFloatUniform("bounds", frame.bounds)
        shader.setIntUniform("colorCount", frame.count)

        /* Clamps protect the shader from values it cannot handle: smoothstep needs edge0 < edge1,
         * and pow() with a non-positive exponent breaks the glow. */
        shader.setFloatUniform("colorBlend", style.colorBlend.coerceAtLeast(0.001f))
        shader.setFloatUniform("edgeSoftness", style.edgeSoftness.coerceAtLeast(0.001f))
        shader.setFloatUniform("glowSpread", style.glowSpread)
        shader.setFloatUniform("depth", style.depth.coerceIn(0f, 1f))

        shader.setFloatUniform("time", time)
        shader.setFloatUniform("seed", seed)
        shader.setFloatUniform("coreRadius", style.coreRadius)
        shader.setFloatUniform("glowIntensity", style.glowIntensity)
        shader.setFloatUniform("warpStrength", style.warpStrength)
        shader.setFloatUniform("patternScale", style.patternScale)
        shader.setFloatUniform("patternDetail", style.patternDetail)

        /* shapeMode numbers MUST match the shader: 0 = blob, 1 = fill, 2 = rounded rect. */
        when (shape) {
            is AuraShape.Blob -> {
                shader.setIntUniform("shapeMode", 0)
                shader.setFloatUniform("shapeParam", shape.exponent.coerceAtLeast(0.5f))
            }
            AuraShape.Fill -> {
                shader.setIntUniform("shapeMode", 1)
                shader.setFloatUniform("shapeParam", 0f)
            }
            is AuraShape.RoundedRect -> {
                shader.setIntUniform("shapeMode", 2)
                shader.setFloatUniform("shapeParam", shape.cornerRadius.coerceAtLeast(0f))
            }
        }

        /* Pattern fit: patternHalf is the half-size of the area in "pattern units" (pixels).
         * Dividing pixel offsets by it normalizes the pattern to the chosen side(s). */
        val fit = when (style.patternFit) {
            AuraPatternFit.Auto ->
                if (shape is AuraShape.Blob) AuraPatternFit.ShorterSide else AuraPatternFit.Stretch
            else -> style.patternFit
        }
        val side = if (fit == AuraPatternFit.LongerSide) maxOf(w, h) else minOf(w, h)
        val halfX = if (fit == AuraPatternFit.Stretch) w / 2f else side / 2f
        val halfY = if (fit == AuraPatternFit.Stretch) h / 2f else side / 2f
        shader.setFloatUniform("patternHalf", halfX, halfY)

        scope.drawRect(brush)
    }
}
