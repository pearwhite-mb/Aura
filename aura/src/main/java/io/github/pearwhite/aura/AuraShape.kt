package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

/**
 * The outline (mask) of the aura. A shape only decides WHERE the aura is visible; what is drawn
 * inside is decided by [AuraStyle] and the palette.
 *
 * Adding a subtype breaks exhaustive `when` in user code (source-incompatible): note it in the changelog.
 *
 * RU: Контур (маска) ауры. Форма определяет только ГДЕ аура видна; что нарисовано внутри,
 * RU: определяют [AuraStyle] и палитра.
 * RU: Добавление нового варианта ломает исчерпывающие `when` у пользователей (на уровне исходников):
 * RU: отмечайте это в changelog.
 */
@Immutable
public sealed interface AuraShape {
    /**
     * Superellipse inscribed into the shorter side, with a soft halo. `exponent = 2` is a circle,
     * larger values give a squircle, 1 gives a diamond.
     * RU: Суперэллипс, вписанный в меньшую сторону, с мягким ореолом. `exponent = 2` — круг,
     * RU: больше — сквиркл, 1 — ромб.
     */
    public data class Blob(val exponent: Float = 2f) : AuraShape

    /**
     * Fills the whole area, no mask. Glow and edge softness are not used.
     * RU: Заливает всю область, без маски. Свечение и размытие края не используются.
     */
    public data object Fill: AuraShape

    /**
     * Fills the area with rounded corners; edges fade softly inward. [cornerRadius] is a fraction
     * of half of the shorter side (1 = fully rounded).
     * RU: Заливает область со скруглёнными углами; края мягко растворяются внутрь. [cornerRadius] —
     * RU: доля половины меньшей стороны (1 = максимально скруглённый).
     */
    public data class RoundedRect(val cornerRadius: Float = 0.3f) : AuraShape
}
