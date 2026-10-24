package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

@Immutable
public data class AuraStyle(
    val coreRadius: Float = 0.5f,     // радиус плотного ядра
    val edgeSoftness: Float = 0.05f,    // размытость границы (> 0)
    val glowIntensity: Float = 0.2f,   // сила свечения вокруг
    val glowSpread: Float = 3.0f,        // больше значение → уже свечение
    val warpStrength: Float = 0.4f,    // «жидкость»: сила искажения
    val patternScale: Float = 2.4f,      // масштаб цветовых пятен
    val colorBlend: Float = 0.25f,      // полуширина перехода между цветами, 0..0.5
    val depth: Float = 1f,            // 0 — плоский узор, 1 — узор обёрнут вокруг шара.
    val patternDetail: Float = 1.2f,  // Мелкая рябь внутри пятен: меньше — глаже
    val patternFit: AuraPatternFit = AuraPatternFit.Auto
)
