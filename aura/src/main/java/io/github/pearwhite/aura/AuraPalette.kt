package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors together with their shares of the aura's area.
 * Instances are immutable and compared by content, which lets Compose skip needless work and
 * lets the color transition restart only when the palette really changes.
 *
 * Order matters: neighbouring colors blend into each other, so put similar hues next to each other
 * and keep the order of categories stable between updates (transitions match entries by index).
 */
@Immutable
public class AuraPalette internal constructor(
    /* Colors in palette order. */
    internal val colors: List<Color>,
    /* Normalized weights (sum = 1), same length as colors. */
    internal val weights: List<Float>,
) {
    /** Number of colors.*/
    public val size: Int get() = colors.size

    /** True if there is nothing to draw. */
    public val isEmpty: Boolean get() = colors.isEmpty()

    override fun equals(other: Any?): Boolean =
        other is AuraPalette && colors == other.colors && weights == other.weights
    override fun hashCode(): Int = 31 * colors.hashCode() + weights.hashCode()
    override fun toString(): String = "AuraPalette(${colors.zip(weights)})"

    public companion object {
        /** An empty palette; [Aura] draws nothing for it.*/
        public val Empty: AuraPalette = AuraPalette(emptyList(), emptyList())

        /**
         * Equal shares for all colors.
         */
        public fun of(colors: List<Color>): AuraPalette = weighted(colors.map { it to 1f })

        /**
         * Vararg convenience for [weighted].
         */
        public fun weighted(vararg entries: Pair<Color, Float>): AuraPalette =
            weighted(entries.asList())

        /**
         * Colors with weights. Weights are proportions and need not sum to 1 (counts can be passed
         * directly). Negative and non-finite weights count as 0; if all weights are 0 the shares are
         * equal. Only the first [AuraDefaults.MaxColors] entries are used. Zero-weight entries are
         * kept on purpose: they are invisible, and a color can then grow smoothly from 0 when data appears.
         */
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
