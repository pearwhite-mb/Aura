package io.github.pearwhite.aura

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

public object AuraDefaults {
    public val Style: AuraStyle = AuraStyle()
    public val Animation: AuraAnimation = AuraAnimation.Flow()
    public const val MaxColors: Int = 8
    public val ColorTransition: AnimationSpec<Float> = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    public val Shape: AuraShape = AuraShape.Blob()

    public val OrbStyle: AuraStyle = AuraStyle(
        coreRadius = 0.5f, edgeSoftness = 0.05f,
        glowIntensity = 0.2f, glowSpread = 3f,
        warpStrength = 0.4f, patternScale = 2.4f, colorBlend = 0.25f,
        depth = 1f, patternDetail = 1.2f,
    )

    public val OrbPalette: AuraPalette = AuraPalette.weighted(
        Color(0xFFFFA63D) to 0.18f,   // жёлто-оранжевый
        Color(0xFFF2602C) to 0.30f,   // оранжевый
        Color(0xFFC8283A) to 0.25f,   // красный
        Color(0xFF5B2C9E) to 0.19f,   // фиолетовый
        Color(0xFFC04FE0) to 0.08f,   // розово-лиловый (верхний край)
    )

    public fun vibrantPalette(count: Int = 4, random: Random = Random.Default): List<Color> =
        List(count.coerceIn(1, MaxColors)) {
            Color.hsv(
                hue = random.nextFloat() * 360f,
                saturation = 0.8f + random.nextFloat() * 0.2f,
                value = 0.85f + random.nextFloat() * 0.15f,
            )
        }
}
