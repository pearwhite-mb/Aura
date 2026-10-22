package io.github.pearwhite.aura.sample

import io.github.pearwhite.aura.AuraPalette
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.pearwhite.aura.*

private val PreviewPalette = AuraPalette.weighted(
    Color(0xFFE53935) to 0.5f,
    Color(0xFF43A047) to 0.3f,
    Color(0xFF1E88E5) to 0.2f,
)

@Composable
private fun PreviewSurface(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.background(Color(0xFF101014)).padding(16.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Preview(name = "Blob, по умолчанию")
@Composable
private fun BlobDefaultPreview() = PreviewSurface {
    Aura(PreviewPalette, Modifier.size(260.dp), animation = AuraAnimation.None)
}

@Preview(name = "Blob, без свечения")
@Composable
private fun BlobNoGlowPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.size(260.dp),
        style = AuraDefaults.Style.copy(glowIntensity = 0f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Blob, резкие границы цветов")
@Composable
private fun BlobSharpPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.size(260.dp),
        style = AuraDefaults.Style.copy(colorBlend = 0.02f, edgeSoftness = 0.03f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Blob, сквиркл")
@Composable
private fun SquirclePreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.size(260.dp),
        shape = AuraShape.Blob(exponent = 4f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Доли 90/5/5")
@Composable
private fun SkewedWeightsPreview() = PreviewSurface {
    Aura(
        AuraPalette.weighted(
            Color(0xFFE53935) to 0.9f,
            Color(0xFF43A047) to 0.05f,
            Color(0xFF1E88E5) to 0.05f,
        ),
        Modifier.size(260.dp),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "RoundedRect, широкая область")
@Composable
private fun RoundedRectPreview() = PreviewSurface {
    Aura(
        PreviewPalette, Modifier.fillMaxWidth().height(160.dp),
        shape = AuraShape.RoundedRect(cornerRadius = 0.4f),
        animation = AuraAnimation.None,
    )
}

@Preview(name = "Fill, весь экран", device = "spec:width=411dp,height=891dp")
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
