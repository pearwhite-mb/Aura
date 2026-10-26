package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

/**
 * Visual parameters of the aura that are independent of its shape and palette.
 * Distances are measured in "half of the shorter side" units (1.0 reaches the edge of the area).
 *
 * Maintainer note: this is a public `data class`. Adding a property changes the constructor,
 * `copy` and `componentN`, which is binary-incompatible. Acceptable before 1.0, but the approach
 * must be reconsidered before the first stable release.
 */
@Immutable
public data class AuraStyle(
    /**
     * Radius of the solid core (opacity is 50% exactly at this radius). Blob only.
     * Also defines the sphere radius when [depth] is used.
     */
    val coreRadius: Float = 0.7f,

    /**
     * Half-width of the core edge fade. Blob: the edge goes from solid to transparent over
     * `2 * edgeSoftness`. RoundedRect: the fade inward lasts about `2 * edgeSoftness`.
     */
    val edgeSoftness: Float = 0.05f,

    /**
     * Opacity of the halo right at the core edge (0..1). 0 disables the halo. Blob only.
     */
    val glowIntensity: Float = 0.4f,

    /**
     * Falloff exponent of the halo: larger values make it fade faster (narrower halo). Blob only.
     */
    val glowSpread: Float = 3.0f,

    /**
     * How strongly coordinates are distorted before the pattern is evaluated ("liquid" look).
     */
    val warpStrength: Float = 0.4f,

    /**
     * Pattern frequency; larger values give smaller spots. The whole object (sphere or area)
     * spans [-0.5, 0.5] in pattern units, so this is "spots per object" regardless of its size.
     */
    val patternScale: Float = 4.0f,

    /**
     * Half-width of the transition between neighbouring palette colors (in 0..1 palette units).
     * Smaller values give sharper boundaries. Segments narrower than `2 * colorBlend` never
     * reach a pure color and show up only as a soft accent.
     */
    val colorBlend: Float = 0.20f,

    /**
     * 0..1. How much the pattern is wrapped around a sphere (0 = flat, 1 = fully spherical).
     * Used by Blob only and designed for a circle (exponent = 2); ignored for other shapes.
     */
    val depth: Float = 1f,

    /**
     * Multiplier of the fine ripple inside the spots. Lower values give smoother gradients.
     */
    val patternDetail: Float = 1.2f,

    /**
     * How the pattern is fitted to non-square areas. Not used by Blob (see [AuraPatternFit]).
     */
    val patternFit: AuraPatternFit = AuraPatternFit.Auto
)
