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
     * Continuous flow.
     * @param speed Time rate multiplier (1 = normal, 0 = frozen time).
     * @param maxFps Upper limit of animation updates per second; null updates on every display frame.
     * Lower values save GPU and battery, but the motion becomes steppier. The real rate is rounded
     * to a multiple of the display refresh period (45 on a 60 Hz display gives 30), so prefer divisors
     * of the refresh rate: 60, 30, 20, 15. Palette transitions are not limited.
     *
     * RU: Непрерывное течение.
     * RU: speed — множитель скорости времени (1 = обычная, 0 = время стоит).
     * RU: maxFps — верхний предел обновлений анимации в секунду; null — на каждом кадре экрана.
     * RU: Меньше значение — меньше нагрузка на GPU и батарею, но движение «ступенчатее». Реальная
     * RU: частота округляется до кратной периоду обновления экрана (45 на экране 60 Гц даст 30), поэтому
     * RU: берите делители частоты экрана: 60, 30, 20, 15. Переходы палитры не ограничиваются.
     */
    public data class Flow(
        public val speed: Float = 1f,
        public val maxFps: Int? = null,
    ) : AuraAnimation
}
