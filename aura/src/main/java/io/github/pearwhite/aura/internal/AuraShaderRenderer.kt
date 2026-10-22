package io.github.pearwhite.aura.internal

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.AuraPatternFit
import io.github.pearwhite.aura.AuraShape
import io.github.pearwhite.aura.AuraStyle

internal class PaletteFrame {
    val colors = FloatArray(AuraDefaults.MaxColors * 4)
    val bounds = FloatArray(AuraDefaults.MaxColors)
    var count = 0
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class AuraShaderRenderer {
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
        shader.setFloatUniform("resolution", scope.size.width, scope.size.height)
        shader.setFloatUniform("colors", frame.colors)
        shader.setFloatUniform("bounds", frame.bounds)
        shader.setIntUniform("colorCount", frame.count)
        shader.setFloatUniform("colorBlend", style.colorBlend.coerceAtLeast(0.001f))
        shader.setFloatUniform("time", time)
        shader.setFloatUniform("seed", seed)
        shader.setFloatUniform("coreRadius", style.coreRadius)
        shader.setFloatUniform("edgeSoftness", style.edgeSoftness.coerceAtLeast(0.001f))
        shader.setFloatUniform("glowIntensity", style.glowIntensity)
        shader.setFloatUniform("glowSpread", style.glowSpread)
        shader.setFloatUniform("warpStrength", style.warpStrength)
        shader.setFloatUniform("patternScale", style.patternScale)
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
        val fit = when (style.patternFit) {
            AuraPatternFit.Auto ->
                if (shape is AuraShape.Blob) AuraPatternFit.ShorterSide else AuraPatternFit.Stretch
            else -> style.patternFit
        }
        val w = scope.size.width.coerceAtLeast(1f)
        val h = scope.size.height.coerceAtLeast(1f)
        val side = if (fit == AuraPatternFit.LongerSide) maxOf(w, h) else minOf(w, h)
        val halfX = if (fit == AuraPatternFit.Stretch) w / 2f else side / 2f
        val halfY = if (fit == AuraPatternFit.Stretch) h / 2f else side / 2f
        shader.setFloatUniform("patternHalf", halfX, halfY)

        scope.drawRect(brush)
    }
}
