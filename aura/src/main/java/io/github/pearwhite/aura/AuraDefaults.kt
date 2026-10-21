package io.github.pearwhite.aura

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

public object AuraDefaults {
    public val Style: AuraStyle = AuraStyle()
    public val Animation: AuraAnimation = AuraAnimation.Flow()
    public const val MaxColors: Int = 8

    public fun vibrantPalette(count: Int = 4, random: Random = Random.Default): List<Color> =
        List(count.coerceIn(1, MaxColors)) {
            Color.hsv(
                hue = random.nextFloat() * 360f,
                saturation = 0.8f + random.nextFloat() * 0.2f,
                value = 0.85f + random.nextFloat() * 0.15f,
            )
        }
}
