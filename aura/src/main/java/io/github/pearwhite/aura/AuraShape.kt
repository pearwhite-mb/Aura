package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

@Immutable
public sealed interface AuraShape {
    public data class Blob(val exponent: Float = 2f) : AuraShape
    public data object Fill: AuraShape
    public data class RoundedRect(val cornerRadius: Float = 0.3f) : AuraShape
}
