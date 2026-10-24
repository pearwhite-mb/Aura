package io.github.pearwhite.aura

/**
 * How pattern coordinates are fitted to a non-square area. Only matters for [AuraShape.Fill]
 * and [AuraShape.RoundedRect]: Blob always uses its own shape coordinates.
 *
 * RU: Как координаты узора подгоняются под неквадратную область. Важно только для
 * RU: [AuraShape.Fill] и [AuraShape.RoundedRect]: Blob всегда использует координаты своей формы.
 */
public enum class AuraPatternFit {
    /**
     * Blob -> [ShorterSide]; Fill and RoundedRect -> [Stretch].
     * RU: Blob -> [ShorterSide]; Fill и RoundedRect -> [Stretch].
     */
    Auto,

    /**
     * Spot size follows the shorter side; on elongated areas the pattern repeats.
     * RU: Размер пятен по меньшей стороне; на вытянутых областях узор повторяется.
     */
    ShorterSide,

    /**
     * Spot size follows the longer side: spots stay round, but only a part of the pattern is
     * visible along the shorter axis.
     * RU: Размер пятен по большей стороне: пятна круглые, но по короткой оси виден лишь фрагмент узора.
     */
    LongerSide,

    /**
     * The pattern is stretched over the whole area; spots stretch with the area's proportions.
     * RU: Узор растягивается на всю область; пятна вытягиваются вместе с её пропорциями.
     */
    Stretch,
}
