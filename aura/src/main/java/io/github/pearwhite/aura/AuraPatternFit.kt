package io.github.pearwhite.aura

/**
 * How pattern coordinates are fitted to a non-square area. Only matters for [AuraShape.Fill]
 * and [AuraShape.RoundedRect]: Blob always uses its own shape coordinates.
 */
public enum class AuraPatternFit {
    /**
     * Blob -> [ShorterSide]; Fill and RoundedRect -> [Stretch].
     */
    Auto,

    /**
     * Spot size follows the shorter side; on elongated areas the pattern repeats.
     */
    ShorterSide,

    /**
     * Spot size follows the longer side: spots stay round, but only a part of the pattern is
     * visible along the shorter axis.
     */
    LongerSide,

    /**
     * The pattern is stretched over the whole area; spots stretch with the area's proportions.
     */
    Stretch,
}
