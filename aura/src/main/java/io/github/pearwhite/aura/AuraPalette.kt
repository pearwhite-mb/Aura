package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


@Immutable
public class AuraPalette internal constructor(
    internal val colors: List<Color>,
    internal val weights: List<Float>,
) {

    public val size: Int get() = colors.size
    public val isEmpty: Boolean get() = colors.isEmpty()

    override fun equals(other: Any?): Boolean =
        other is AuraPalette && colors == other.colors && weights == other.weights

    override fun hashCode(): Int = 31 * colors.hashCode() + weights.hashCode()
    override fun toString(): String = "AuraPalette(${colors.zip(weights)})"

    public companion object {
        public val Empty: AuraPalette = AuraPalette(emptyList(), emptyList())

        public fun of(colors: List<Color>): AuraPalette = weighted(colors.map { it to 1f })

        public fun weighted(vararg entries: Pair<Color, Float>): AuraPalette =
            weighted(entries.asList())

        public fun weighted(entries: List<Pair<Color, Float>>): AuraPalette {
            val used = entries.take(AuraDefaults.MaxColors)
            if (used.isEmpty()) return Empty
            val raw = used.map { (_, w) -> if (w.isFinite()) w.coerceAtLeast(0f) else 0f }
            val sum = raw.sum()
            val weights = if (sum > 0f) raw.map { it / sum } else List(used.size) { 1f / used.size }
            return AuraPalette(used.map { it.first }, weights)
        }
    }
}
