package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

/**
 * How the aura moves over time.
 * Warning for tests: an endless animation keeps Compose busy, so UI tests that wait for idle hang.
 * Use [None] or control the clock manually (`mainClock.autoAdvance = false`).
 *
 * RU: Как аура движется во времени.
 * RU: Важно для тестов: бесконечная анимация не даёт Compose «успокоиться», и UI-тесты с ожиданием
 * RU: простоя зависают. Используйте [None] или управляйте часами вручную (`mainClock.autoAdvance = false`).
 */
@Immutable
public sealed interface AuraAnimation {
    /**
     * Static aura. Time stays where it stopped (0 on first composition).
     * RU: Статичная аура. Время остаётся там, где остановилось (0 при первой композиции).
     */
    public data object None: AuraAnimation

    /**
     * Continuous flow. [speed] multiplies the time rate (1 = normal, 0 behaves like frozen time).
     * RU: Непрерывное течение. [speed] — множитель скорости времени (1 = обычная, 0 = время стоит).
     */
    public data class Flow(val speed: Float = 1f) : AuraAnimation
}
