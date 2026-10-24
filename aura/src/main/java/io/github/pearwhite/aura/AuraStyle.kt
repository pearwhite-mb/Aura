package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

/**
 * Visual parameters of the aura that are independent of its shape and palette.
 * Distances are measured in "half of the shorter side" units (1.0 reaches the edge of the area).
 *
 * Maintainer note: this is a public `data class`. Adding a property changes the constructor,
 * `copy` and `componentN`, which is binary-incompatible. Acceptable before 1.0, but the approach
 * must be reconsidered before the first stable release.
 *
 * RU: Визуальные параметры ауры, не зависящие от формы и палитры.
 * RU: Расстояния измеряются в долях половины меньшей стороны (1.0 доходит до края области).
 * RU: Заметка для мейнтейнера: это публичный data class. Добавление поля меняет конструктор,
 * RU: copy и componentN, то есть ломает бинарную совместимость. До 1.0 допустимо, но перед
 * RU: первым стабильным релизом подход нужно пересмотреть.
 */
@Immutable
public data class AuraStyle(
    /**
     * Radius of the solid core (opacity is 50% exactly at this radius). Blob only.
     * Also defines the sphere radius when [depth] is used.
     * RU: Радиус плотного ядра (на этом радиусе прозрачность ровно 50%). Только для Blob.
     * RU: Также задаёт радиус шара при использовании [depth].
     */
    val coreRadius: Float = 0.7f,

    /**
     * Half-width of the core edge fade. Blob: the edge goes from solid to transparent over
     * `2 * edgeSoftness`. RoundedRect: the fade inward lasts about `2 * edgeSoftness`.
     * RU: Полуширина размытия края. Blob: край переходит от плотного к прозрачному на `2 * edgeSoftness`.
     * RU: RoundedRect: размытие внутрь длится около `2 * edgeSoftness`.
     */
    val edgeSoftness: Float = 0.05f,

    /**
     * Opacity of the halo right at the core edge (0..1). 0 disables the halo. Blob only.
     * RU: Прозрачность ореола прямо у границы ядра (0..1). 0 отключает ореол. Только Blob.
     */
    val glowIntensity: Float = 0.4f,

    /**
     * Falloff exponent of the halo: larger values make it fade faster (narrower halo). Blob only.
     * RU: Степень затухания ореола: больше значение, быстрее затухание (уже ореол). Только Blob.
     */
    val glowSpread: Float = 3.0f,

    /**
     * How strongly coordinates are distorted before the pattern is evaluated ("liquid" look).
     * RU: Насколько сильно искажаются координаты перед расчётом узора (эффект «жидкости»).
     */
    val warpStrength: Float = 0.4f,

    /**
     * Pattern frequency; larger values give smaller spots. The whole object (sphere or area)
     * spans [-0.5, 0.5] in pattern units, so this is "spots per object" regardless of its size.
     * RU: Частота узора: больше значение, мельче пятна. Объект (шар или область) занимает [-0.5, 0.5]
     * RU: в единицах узора, поэтому это «пятен на объект» независимо от размера.
     */
    val patternScale: Float = 4.0f,

    /**
     * Half-width of the transition between neighbouring palette colors (in 0..1 palette units).
     * Smaller values give sharper boundaries. Segments narrower than `2 * colorBlend` never
     * reach a pure color and show up only as a soft accent.
     * RU: Полуширина перехода между соседними цветами палитры (в единицах палитры 0..1).
     * RU: Меньше значение, резче границы. Сегменты уже `2 * colorBlend` не доходят до чистого цвета
     * RU: и проявляются как мягкий акцент.
     */
    val colorBlend: Float = 0.20f,

    /**
     * 0..1. How much the pattern is wrapped around a sphere (0 = flat, 1 = fully spherical).
     * Used by Blob only and designed for a circle (exponent = 2); ignored for other shapes.
     * RU: 0..1. Насколько узор «обёрнут» вокруг шара (0 = плоский, 1 = полностью сферический).
     * RU: Только для Blob, рассчитано на круг (exponent = 2); для других форм игнорируется.
     */
    val depth: Float = 1f,

    /**
     * Multiplier of the fine ripple inside the spots. Lower values give smoother gradients.
     * RU: Множитель мелкой ряби внутри пятен. Меньше значение, глаже градиенты.
     */
    val patternDetail: Float = 1.2f,

    /**
     * How the pattern is fitted to non-square areas. Not used by Blob (see [AuraPatternFit]).
     * RU: Как узор подгоняется под неквадратные области. Для Blob не используется (см. [AuraPatternFit]).
     */
    val patternFit: AuraPatternFit = AuraPatternFit.Auto
)
