package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
public class AuraColor(
    public val color: Color,
    public val weight: Float = 1f,
)

@Immutable
public class AuraPalette private constructor(internal val entries: List<AuraColor>) {

    override fun equals(other: Any?): Boolean = other is AuraPalette &&
            entries.size == other.entries.size &&
            entries.indices.all {
                entries[it].color == other.entries[it].color &&
                        entries[it].weight == other.entries[it].weight
            }

    override fun hashCode(): Int =
        entries.fold(0) { acc, e -> 31 * (31 * acc + e.color.hashCode()) + e.weight.hashCode() }

    public companion object {
        public fun of(colors: List<Color>): AuraPalette =
            AuraPalette(colors.take(AuraDefaults.MaxColors).map { AuraColor(it) })

        public fun weighted(colors: List<AuraColor>): AuraPalette =
            AuraPalette(
                colors.take(AuraDefaults.MaxColors)
                    .map { AuraColor(it.color, it.weight.coerceAtLeast(0f)) })
    }
}
