# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). While the version is `0.x`, the public API may change between minor versions; every such change is listed here. Changes that break binary compatibility (for example, adding a parameter to a public composable or a data class) and changes that can break exhaustive `when` expressions (new variants of a `sealed` type or an `enum`) are called out explicitly.

## [Unreleased]

## [0.1.0] - Unreleased

First public release.

### Added

- `Aura` composable that draws an animated aura with a single AGSL shader (Android 13+), with a gradient fallback below.
- `AuraPalette` with up to 8 colors and optional weights (shares of the area); smooth transitions between palettes.
- Shapes: `AuraShape.Blob`, `AuraShape.RoundedRect` and `AuraShape.Fill`.
- `AuraStyle` with parameters for the core, edge, halo, warp, pattern scale and detail, color blending, spherical depth and pattern fit (`AuraPatternFit`).
- `AuraAnimation.Flow` (with `speed` and an optional `maxFps` limit) and `AuraAnimation.None`.
- `AuraDefaults` with a default palette and the `vibrantPalette` and `harmonicPalette` generators.
- Animation pauses when the lifecycle drops below `STARTED` and respects the system animation scale.
