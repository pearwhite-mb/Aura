package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

@Immutable
public data class AuraStyle(
    val coreRadius: Float = 0.55f,     // радиус плотного ядра
    val edgeSoftness: Float = 0.1f,    // размытость границы (> 0)
    val glowIntensity: Float = 0.4f,   // сила свечения вокруг
    val glowSpread: Float = 5f,        // больше значение → уже свечение
    val warpStrength: Float = 0.4f,    // «жидкость»: сила искажения
    val patternScale: Float = 6f,      // масштаб цветовых пятен
    val shapeExponent: Float = 2f,     // 2 = круг, 4+ = сквиркл, 1 = ромб
)
