package io.github.pearwhite.aura.internal

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.pearwhite.aura.AuraDefaults
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
    private val colorBuffer = FloatArray(AuraDefaults.MaxColors * 4)

    fun draw(
        scope: DrawScope,
        frame: PaletteFrame,
        style: AuraStyle,
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
        shader.setFloatUniform("shapeExponent", style.shapeExponent.coerceAtLeast(0.5f))

        scope.drawRect(brush)
    }
}
