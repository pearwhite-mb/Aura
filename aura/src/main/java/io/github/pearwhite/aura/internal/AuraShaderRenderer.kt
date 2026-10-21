package io.github.pearwhite.aura.internal

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.AuraStyle

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class AuraShaderRenderer {
    private val shader = RuntimeShader(AURA_SHADER_SRC)
    private val brush = ShaderBrush(shader)
    private val colorBuffer = FloatArray(AuraDefaults.MaxColors * 4)

    fun draw(
        scope: DrawScope,
        colors: List<Color>,
        style: AuraStyle,
        seed: Float,
        time: Float,
    ) {
        val n = colors.size.coerceIn(1, AuraDefaults.MaxColors)
        for (i in 0 until n) {
            val c = colors[i]
            colorBuffer[i * 4] = c.red
            colorBuffer[i * 4 + 1] = c.green
            colorBuffer[i * 4 + 2] = c.blue
            colorBuffer[i * 4 + 3] = c.alpha
        }
        shader.setFloatUniform("resolution", scope.size.width, scope.size.height)
        shader.setFloatUniform("colors", colorBuffer)
        shader.setIntUniform("colorCount", n)
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
