package io.github.pearwhitemb.aura

import androidx.compose.runtime.Immutable

/**
 * How the aura moves over time.
 * Warning for tests: an endless animation keeps Compose busy, so UI tests that wait for idle hang.
 * Use [None] or control the clock manually (`mainClock.autoAdvance = false`).
 */
@Immutable
public sealed interface AuraAnimation {
    /**
     * Static aura. Time stays where it stopped (0 on first composition).
     */
    public data object None: AuraAnimation

    /**
     * Continuous flow.
     * @param speed Time rate multiplier (1 = normal, 0 = frozen time).
     * @param maxFps Upper limit of animation updates per second; null updates on every display frame.
     * Lower values save GPU and battery, but the motion becomes steppier. The real rate is rounded
     * to a multiple of the display refresh period (45 on a 60 Hz display gives 30), so prefer divisors
     * of the refresh rate: 60, 30, 20, 15. Palette transitions are not limited.
     */
    public data class Flow(
        public val speed: Float = 1f,
        public val maxFps: Int? = null,
    ) : AuraAnimation
}
