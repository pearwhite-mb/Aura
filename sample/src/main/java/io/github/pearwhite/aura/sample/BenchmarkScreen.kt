package io.github.pearwhite.aura.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.pearwhite.aura.Aura
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.AuraPalette
import io.github.pearwhite.aura.AuraShape
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

enum class Scenario {
    BlobSmall,
    FillFullScreen,
    FillEightColorsDetailed,
    RoundedRectCard,
    PaletteSwitching,
}

@Composable
fun BenchmarkScreen(scenario: Scenario) {
    val palette = AuraDefaults.Palette
    when (scenario) {
        Scenario.BlobSmall ->
            Box(Modifier.fillMaxSize(), Alignment.Center) { Aura(palette, Modifier.size(300.dp)) }

        Scenario.FillFullScreen ->
            Aura(palette, Modifier.fillMaxSize(), shape = AuraShape.Fill)

        Scenario.FillEightColorsDetailed ->
            Aura(
                palette = AuraPalette.of(AuraDefaults.harmonicPalette(8, Random(1))),
                modifier = Modifier.fillMaxSize(),
                style = AuraDefaults.Style.copy(patternScale = 6f, patternDetail = 4.5f),
                shape = AuraShape.Fill,
            )

        Scenario.RoundedRectCard ->
            Box(Modifier
                .fillMaxSize()
                .padding(16.dp), Alignment.Center) {
                Aura(
                    palette = palette,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = AuraShape.RoundedRect(0.3f)
                )
            }

        Scenario.PaletteSwitching -> {
            var i by remember() { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(1_000.milliseconds); i++
                }
            }
            val p = remember(i) {
                AuraPalette.weighted(AuraDefaults.harmonicPalette(5, Random(i)).map { it to 1f })
            }
            Aura(p, Modifier.fillMaxSize(), shape = AuraShape.Fill)
        }
    }
}
