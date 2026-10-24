package io.github.pearwhite.aura

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Default values and helpers. The defaults reproduce the "orb" look (soft sphere with a halo).
 * RU: Значения по умолчанию и помощники. Дефолты дают вид «шара» (мягкая сфера с ореолом).
 */
public object AuraDefaults {
    /** Default style. RU: Стиль по умолчанию. */
    public val Style: AuraStyle = AuraStyle()
    /** Default shape. RU: Форма по умолчанию. */
    public val Shape: AuraShape = AuraShape.Blob()
    /** Default animation. RU: Анимация по умолчанию. */
    public val Animation: AuraAnimation = AuraAnimation.Flow()
    /** Spec for smooth palette changes. RU: Анимация плавной смены палитры. */
    public val ColorTransition: AnimationSpec<Float> = tween(durationMillis = 800, easing = FastOutSlowInEasing)

    /*
     * Hard limit of colors: the shader uses fixed-size uniform arrays of 8 elements, so this value
     * must match them. It is `const` on purpose: AuraPalette reads it while AuraDefaults is being
     * initialized (see [Palette]); a const is inlined at compile time and avoids an init cycle.
     * RU: Жёсткий лимит цветов: в шейдере массивы uniform фиксированного размера 8, значение должно
     * RU: совпадать. Это `const` намеренно: AuraPalette читает его во время инициализации AuraDefaults
     * RU: (см. [Palette]); константа подставляется при компиляции и не создаёт цикл инициализации.
     */
    public const val MaxColors: Int = 8

    /**
     * A ready-made palette in the spirit of the reference design (warm to deep purple).
     * RU: Готовая палитра в духе референса (от тёплых тонов к глубокому фиолетовому).
     */
    public val Palette: AuraPalette = AuraPalette.weighted(
        Color(0xFFFFA63D) to 0.18f,
        Color(0xFFF2602C) to 0.30f,
        Color(0xFFC8283A) to 0.25f,
        Color(0xFF5B2C9E) to 0.19f,
        Color(0xFFC04FE0) to 0.08f,
    )

    /**
     * Random bright colors with independent hues. Note: value is at least 0.85, so no dark tones.
     * Unrelated hues side by side can blend into muddy mixes.
     * RU: Случайные яркие цвета с независимыми оттенками. Яркость не ниже 0.85, тёмных тонов не будет.
     * RU: Далёкие по оттенку соседи могут смешиваться в «грязные» цвета.
     */
    public fun vibrantPalette(count: Int = 4, random: Random = Random.Default): List<Color> =
        List(count.coerceIn(1, MaxColors)) {
            Color.hsv(
                hue = random.nextFloat() * 360f,
                saturation = 0.8f + random.nextFloat() * 0.2f,
                value = 0.85f + random.nextFloat() * 0.15f,
            )
        }

    /**
     * Colors laid along an arc of the hue circle that darken towards the end. Neighbours are close
     * in hue, so transitions look clean.
     * RU: Цвета лежат на дуге круга оттенков и к концу темнеют. Соседи близки по оттенку,
     * RU: поэтому переходы выглядят чище.
     */
    public fun harmonicPalette(count: Int = 4, random: Random = Random): List<Color> {
        val n = count.coerceIn(1, MaxColors)
        val start = random.nextFloat() * 360f
        val arc = 80f + random.nextFloat() * 140f       // arc length 80..220 degrees
        val direction = if (random.nextBoolean()) 1f else -1f
        val darkening = 0.2f + random.nextFloat() * 0.3f
        return List(n) { i ->
            val f = if (n == 1) 0f else i / (n - 1f)
            Color.hsv(
                /* Wrap into [0, 360): the modulo of a negative number is negative in Kotlin.
                 * RU: Приводим к [0, 360): остаток от деления отрицательного числа в Kotlin отрицательный. */
                hue = ((start + direction * arc * f) % 360f + 360f) % 360f,
                saturation = 0.7f + random.nextFloat() * 0.3f,
                value = (1f - darkening * f + (random.nextFloat() - 0.5f) * 0.1f).coerceIn(0.5f, 1f),
            )
        }
    }
}
