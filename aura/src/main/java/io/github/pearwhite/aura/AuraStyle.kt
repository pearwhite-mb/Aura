package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

@Immutable
public data class AuraStyle(
    val coreRadius: Float = 0.55f,     // радиус плотного ядра
    val edgeSoftness: Float = 0.075f,    // размытость границы (> 0)
    val glowIntensity: Float = 0.6f,   // сила свечения вокруг
    val glowSpread: Float = 6.0f,        // больше значение → уже свечение
    val warpStrength: Float = 0.4f,    // «жидкость»: сила искажения
    val patternScale: Float = 6f,      // масштаб цветовых пятен
    val colorBlend: Float = 0.2f,      // полуширина перехода между цветами, 0..0.5
    val patternFit: AuraPatternFit = AuraPatternFit.Auto
)
