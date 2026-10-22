package io.github.pearwhite.aura.sample

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.pearwhite.aura.Aura
import io.github.pearwhite.aura.AuraAnimation
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.AuraPalette
import io.github.pearwhite.aura.AuraShape
import io.github.pearwhite.aura.AuraStyle
import java.util.Locale


private enum class ShapeKind(val title: String) {
    Blob("Blob"),
    RoundedRect("RoundedRect"),
    Fill("Fill")
}

private enum class AreaKind(val title: String) {
    Square("1:1"),
    Wide("Широкая")
}

private data class AuraEntry(val color: Color, val weight: Float)

private val InitialEntries = listOf(
    AuraEntry(Color(0xFFE53935), 0.5f),
    AuraEntry(Color(0xFF43A047), 0.3f),
    AuraEntry(Color(0xFF1E88E5), 0.2f),
)

private class StyleParam(
    val name: String,
    val range: ClosedFloatingPointRange<Float>,
    val get: (AuraStyle) -> Float,
    val set: (AuraStyle, Float) -> AuraStyle,
)

private val StyleParams = listOf(
    StyleParam("coreRadius", 0.1f..1f, { it.coreRadius }, { s, v -> s.copy(coreRadius = v) }),
    StyleParam("edgeSoftness", 0.01f..0.5f, { it.edgeSoftness }, { s, v -> s.copy(edgeSoftness = v) }),
    StyleParam("glowIntensity", 0f..1f, { it.glowIntensity }, { s, v -> s.copy(glowIntensity = v) }),
    StyleParam("glowSpread", 0.5f..10f, { it.glowSpread }, { s, v -> s.copy(glowSpread = v) }),
    StyleParam("warpStrength", 0f..1f, { it.warpStrength }, { s, v -> s.copy(warpStrength = v) }),
    StyleParam("patternScale", 1f..15f, { it.patternScale }, { s, v -> s.copy(patternScale = v) }),
    StyleParam("colorBlend", 0.01f..0.4f, { it.colorBlend }, { s, v -> s.copy(colorBlend = v) }),
)

private fun fmt(v: Float): String = "%.2f".format(Locale.ROOT, v)

private fun AuraStyle.toCode(): String =
    StyleParams.joinToString(separator = ",\n", prefix = "AuraStyle(\n", postfix = "\n)") {
        "    ${it.name} = ${fmt(it.get(this))}f"
    }

@Composable
fun AuraPlayground(modifier: Modifier = Modifier) {
    var style by remember { mutableStateOf(AuraDefaults.Style) }
    var kind by remember { mutableStateOf(ShapeKind.Blob) }
    var exponent by remember { mutableFloatStateOf(2f) }
    var corner by remember { mutableFloatStateOf(0.3f) }
    var area by remember { mutableStateOf(AreaKind.Square) }
    var speed by remember { mutableFloatStateOf(1f) }
    var entries by remember { mutableStateOf(InitialEntries) }

    val palette = remember(entries) {
        AuraPalette.weighted(entries.map { it.color to it.weight })
    }
    val shape: AuraShape = when (kind) {
        ShapeKind.Blob -> AuraShape.Blob(exponent)
        ShapeKind.RoundedRect -> AuraShape.RoundedRect(corner)
        ShapeKind.Fill -> AuraShape.Fill
    }
    val animation: AuraAnimation =
        if (speed == 0f) AuraAnimation.None else AuraAnimation.Flow(speed)

    Column(modifier.fillMaxSize()) {
        // Аура закреплена сверху, чтобы видеть её во время движения слайдеров
        Box(
            Modifier.fillMaxWidth().height(292.dp).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Aura(
                palette = palette,
                style = style,
                shape = shape,
                animation = animation,
                colorTransition = tween(150),
                modifier = when (area) {
                    AreaKind.Square -> Modifier.size(260.dp)
                    AreaKind.Wide -> Modifier.fillMaxSize()
                },
            )
        }
        HorizontalDivider()

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            SectionTitle("Область и форма")
            ChipRow(AreaKind.entries, area, { it.title }) { area = it }
            ChipRow(ShapeKind.entries, kind, { it.title }) { kind = it }
            when (kind) {
                ShapeKind.Blob -> ParamSlider("Blob.exponent", exponent, 0.5f..8f) { exponent = it }
                ShapeKind.RoundedRect -> ParamSlider("RoundedRect.cornerRadius", corner, 0f..1f) { corner = it }
                ShapeKind.Fill -> Unit
            }

            SectionTitle("AuraStyle")
            Text(
                "glow* работают только в Blob, edgeSoftness — в Blob и RoundedRect",
                style = MaterialTheme.typography.bodySmall,
            )
            StyleParams.forEach { p ->
                ParamSlider(p.name, p.get(style), p.range) { style = p.set(style, it) }
            }
            TextButton(onClick = { style = AuraDefaults.Style }) { Text("Сбросить стиль") }
            SelectionContainer {
                Text(
                    style.toCode(),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            SectionTitle("Анимация")
            ParamSlider("Скорость (0 = без анимации)", speed, 0f..3f) { speed = it }

            SectionTitle("Палитра (веса)")
            entries.forEachIndexed { i, e ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(20.dp).clip(CircleShape).background(color = e.color))
                    Slider(
                        value = e.weight,
                        onValueChange = { w ->
                            entries = entries.toMutableList().also { it[i] = e.copy(weight = w) }
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    enabled = entries.size < AuraDefaults.MaxColors,
                    onClick = { entries = entries + AuraEntry(AuraDefaults.vibrantPalette(1).first(), 0.5f) },
                ) { Text("+ цвет") }
                OutlinedButton(
                    enabled = entries.size > 1,
                    onClick = { entries = entries.dropLast(1) },
                ) { Text("− цвет") }
                OutlinedButton(
                    onClick = {
                        entries = entries.map { it.copy(color = AuraDefaults.vibrantPalette(1).first()) }
                    },
                ) { Text("Новые цвета") }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun ParamSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(fmt(value), style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}

@Composable
private fun <T> ChipRow(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach {
            FilterChip(selected = it == selected, onClick = { onSelect(it) }, label = { Text(label(it)) })
        }
    }
}
