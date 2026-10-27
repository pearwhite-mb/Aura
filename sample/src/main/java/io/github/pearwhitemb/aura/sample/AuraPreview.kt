package io.github.pearwhitemb.aura.sample

import io.github.pearwhitemb.aura.AuraPalette
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.pearwhitemb.aura.*

private val PreviewPalette = AuraPalette.weighted(
    Color(0xFFFFA63D) to 0.5f,
    Color(0xFFC8283A) to 0.3f,
    Color(0xFF5B2C9E) to 0.2f,
)

@Composable
private fun PreviewSurface(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.background(Color(0xFF101014)).padding(16.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Preview(name = "Blob, default")
@Composable
private fun BlobDefaultPreview() = PreviewSurface {
    Aura(PreviewPalette, Modifier.size(260.dp), animation = AuraAnimation.None)
}

@Preview(name = "Blob, no glow")
@Composable
private fun BlobNoGlowPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.size(260.dp),
        style = AuraDefaults.Style.copy(glowIntensity = 0f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Blob, sharp color edges")
@Composable
private fun BlobSharpPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.size(260.dp),
        style = AuraDefaults.Style.copy(colorBlend = 0.02f, edgeSoftness = 0.03f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Blob, squircle")
@Composable
private fun SquirclePreview() = PreviewSurface {
    Aura(
        palette = PreviewPalette,
        modifier = Modifier.size(260.dp),
        style = AuraDefaults.Style.copy(depth = 0f),
        shape = AuraShape.Blob(exponent = 4f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Weights 90/5/5")
@Composable
private fun SkewedWeightsPreview() = PreviewSurface {
    Aura(
        PreviewPalette,
        Modifier.size(260.dp),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "RoundedRect, wide area")
@Composable
private fun RoundedRectPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.fillMaxWidth().height(160.dp),
        shape = AuraShape.RoundedRect(cornerRadius = 0.4f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Fill, full screen", device = "spec:width=411dp,height=891dp")
@Composable
private fun FillScreenPreview() {
    Aura(
        PreviewPalette, Modifier.fillMaxSize(),
        shape = AuraShape.Fill,
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Playground", showSystemUi = true)
@Composable
private fun PlaygroundPreview() = AuraPlayground()
