package io.github.pearwhite.aura.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.pearwhite.aura.Aura
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.AuraPalette
import io.github.pearwhite.aura.sample.ui.theme.AuraTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuraGeneratorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AuraGeneratorScreen(modifier: Modifier = Modifier) {
    var entries by remember { mutableStateOf(preset(0.5f, 0.3f, 0.2f)) }
    var colorBlend by remember { mutableFloatStateOf(AuraDefaults.Style.colorBlend) }

    val palette = remember(entries) {
        AuraPalette.weighted(entries.map { it.color to it.weight })
    }
    val style = remember(colorBlend) { AuraDefaults.Style.copy(colorBlend = colorBlend) }

    // Доли, как их видит библиотека (если все веса нулевые, доли равные)
    val total = entries.sumOf { it.weight.toDouble() }.toFloat()
    val shares = if (total > 0f) entries.map { it.weight / total }
    else entries.map { 1f / entries.size }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Aura(
            palette = palette,
            style = style,
            colorTransition = tween(200),   // короткий переход, чтобы слайдер не «лагал»
            modifier = Modifier.size(300.dp),
        )

        Spacer(Modifier.height(16.dp))

        // Полоска с реальными долями: сравнивайте её с аурой
        Row(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            entries.forEachIndexed { i, e ->
                if (shares[i] > 0f) {
                    Box(Modifier.weight(shares[i]).fillMaxHeight().background(e.color))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        entries.forEachIndexed { i, e ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).clip(CircleShape).background(e.color))
                Slider(
                    value = e.weight,
                    onValueChange = { w ->
                        entries = entries.toMutableList().also { it[i] = e.copy(weight = w) }
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                )
                Text("${(shares[i] * 100).roundToInt()}%", Modifier.width(44.dp))
            }
        }

        Text("Размытость границ цветов: ${"%.2f".format(colorBlend)}")
        Slider(
            value = colorBlend,
            onValueChange = { colorBlend = it },
            valueRange = 0.01f..0.4f,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { entries = preset(0.7f, 0.3f) }) { Text("70/30") }
            Button(onClick = { entries = preset(0.5f, 0.3f, 0.2f) }) { Text("50/30/20") }
            Button(onClick = { entries = preset(0.9f, 0.05f, 0.05f) }) { Text("90/5/5") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = entries.size < AuraDefaults.MaxColors,
                onClick = { entries = entries + AuraEntry(randomColor(), 0.5f) },
            ) { Text("+ цвет") }
            Button(
                enabled = entries.size > 1,
                onClick = { entries = entries.dropLast(1) },
            ) { Text("− цвет") }
            Button(onClick = { entries = entries.map { it.copy(color = randomColor()) } }) {
                Text("Новые цвета")
            }
        }
    }
}

private data class AuraEntry(val color: Color, val weight: Float)
private val RgbColors = listOf(Color(0xFFE53935), Color(0xFF43A047), Color(0xFF1E88E5))

private fun preset(vararg weights: Float) = weights.mapIndexed { i, w -> AuraEntry(RgbColors[i], w) }

private fun randomColor() = AuraDefaults.vibrantPalette(1).first()
