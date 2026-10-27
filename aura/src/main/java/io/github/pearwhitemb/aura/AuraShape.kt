package io.github.pearwhitemb.aura

import androidx.compose.runtime.Immutable

/**
 * The outline (mask) of the aura. A shape only decides WHERE the aura is visible; what is drawn
 * inside is decided by [AuraStyle] and the palette.
 *
 * Adding a subtype breaks exhaustive `when` in user code (source-incompatible): note it in the changelog.
 */
@Immutable
public sealed interface AuraShape {
    /**
     * Superellipse inscribed into the shorter side, with a soft halo. `exponent = 2` is a circle,
     * larger values give a squircle, 1 gives a diamond.
     */
    public data class Blob(val exponent: Float = 2f) : AuraShape

    /**
     * Fills the whole area, no mask. Glow and edge softness are not used.
     */
    public data object Fill: AuraShape

    /**
     * Fills the area with rounded corners; edges fade softly inward. [cornerRadius] is a fraction
     * of half of the shorter side (1 = fully rounded).
     */
    public data class RoundedRect(val cornerRadius: Float = 0.3f) : AuraShape
}
