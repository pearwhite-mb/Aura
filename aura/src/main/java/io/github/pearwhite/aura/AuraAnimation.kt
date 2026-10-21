package io.github.pearwhite.aura

import androidx.compose.runtime.Immutable

@Immutable
public sealed interface AuraAnimation {
    public data object None: AuraAnimation
    public data class Flow(val speed: Float = 1f) : AuraAnimation
}
