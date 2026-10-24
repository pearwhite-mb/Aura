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

    public fun vibrantPalette(count: Int = 4, random: Random = Random.Default): List<Color> =
        List(count.coerceIn(1, MaxColors)) {
            Color.hsv(
                hue = random.nextFloat() * 360f,
                saturation = 0.8f + random.nextFloat() * 0.2f,
                value = 0.85f + random.nextFloat() * 0.15f,
            )
        }

    public fun harmonicPalette(count: Int = 4, random: Random = Random): List<Color> {
        val n = count.coerceIn(1, MaxColors)
        val start = random.nextFloat() * 360f
        val arc = 80f + random.nextFloat() * 140f
        val direction = if (random.nextBoolean()) 1f else -1f
        val darkening = 0.2f + random.nextFloat() * 0.3f
        return List(n) { i ->
            val f = if (n == 1) 0f else i / (n - 1f)
            Color.hsv(
                hue = ((start + direction * arc * f) % 360f + 360f) % 360f,
                saturation = 0.7f + random.nextFloat() * 0.3f,
                value = (1f - darkening * f + (random.nextFloat() - 0.5f) * 0.1f).coerceIn(0.5f, 1f),
            )
        }
    }
}
