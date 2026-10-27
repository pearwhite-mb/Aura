package io.github.pearwhitemb.aura

import android.graphics.RuntimeShader
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import io.github.pearwhitemb.aura.internal.AURA_SHADER_SRC
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SdkSuppress(minSdkVersion = 33)
@RunWith(AndroidJUnit4::class)
class AuraRenderingTest {
    @get:Rule val rule = createComposeRule()

    private fun setAura(content: @Composable BoxScope.() -> Unit) = rule.setContent {
        Box(Modifier.size(200.dp).background(Color.Black).testTag("area"), content = content)
    }

    private fun pixels() = rule.onNodeWithTag("area").captureToImage().toPixelMap()
    private fun center(): Color = pixels().let { it[it.width / 2, it.height / 2] }

    @Test fun shaderSourceCompiles() {
        RuntimeShader(AURA_SHADER_SRC)
    }

    @Test fun blobLeavesCornersUntouchedAndCenterPainted() {
        setAura {
            Aura(AuraPalette.of(listOf(Color.Red)), Modifier.fillMaxSize(), animation = AuraAnimation.None)
        }
        val px = pixels()
        assertTrue(px[2, 2].red < 0.02f)
        assertTrue(px[px.width / 2, px.height / 2].red > 0.95f)
    }

    @Test fun blobHaloFadesWithoutShelf() {
        val style = AuraStyle(coreRadius = 0.5f, edgeSoftness = 0.05f, glowIntensity = 0.2f, glowSpread = 3f)
        setAura {
            Aura(
                AuraPalette.of(listOf(Color.Red)), Modifier.fillMaxSize(),
                style = style, animation = AuraAnimation.None,
            )
        }
        val px = pixels()
        val y = px.height / 2
        val half = px.width / 2
        fun redAt(dist: Float) = px[half + (dist * half).toInt(), y].red

        val edgeEnd = style.coreRadius + 2 * style.edgeSoftness

        assertTrue(redAt(style.coreRadius * 0.7f) > 0.9f)
        assertTrue(redAt(edgeEnd) <= style.glowIntensity + 0.05f)

        val halo = listOf(0f, 0.1f, 0.2f, 0.3f).map { redAt(edgeEnd + it) }
        halo.zipWithNext().forEach { (a, b) ->
            assertTrue("halo must not grow: $halo", b <= a + 0.01f)
        }
    }

    @Test fun fillCoversTheWholeArea() {
        setAura {
            Aura(
                AuraPalette.of(listOf(Color.Red)), Modifier.fillMaxSize(),
                shape = AuraShape.Fill, animation = AuraAnimation.None,
            )
        }
        val px = pixels()
        assertTrue(px[2, 2].red > 0.95f)
        assertTrue(px[px.width - 3, px.height - 3].red > 0.95f)
    }

    @Test fun allShapesAndPatternFitsRenderWithoutCrash() {
        var shape by mutableStateOf<AuraShape>(AuraShape.Fill)
        var fit by mutableStateOf(AuraPatternFit.Auto)
        setAura {
            Aura(
                AuraPalette.of(listOf(Color(0xFFFFCC00))), Modifier.fillMaxSize(),
                style = AuraDefaults.Style.copy(patternFit = fit),
                shape = shape, animation = AuraAnimation.None,
            )
        }
        val shapes = listOf(AuraShape.Blob(), AuraShape.Blob(4f), AuraShape.RoundedRect(0.3f), AuraShape.Fill)
        for (s in shapes) for (f in AuraPatternFit.entries) {
            shape = s; fit = f
            rule.waitForIdle()
            assertTrue("$s / $f", center().red > 0.9f)
        }
    }

    @Test fun fillSharesFollowWeights() {
        var seed by mutableFloatStateOf(0f)
        val colors = listOf(Color.Red, Color.Green, Color.Blue)
        setAura {
            Aura(
                AuraPalette.weighted(Color.Red to 0.5f, Color.Green to 0.3f, Color.Blue to 0.2f),
                Modifier.fillMaxSize(), shape = AuraShape.Fill,
                animation = AuraAnimation.None, seed = seed,
            )
        }
        val acc = FloatArray(3)
        val n = 12
        repeat(n) { i ->
            seed = i * 8.3f
            rule.waitForIdle()
            val s = pixels().shares(colors)
            for (k in acc.indices) acc[k] += s[k] / n
        }
        assertEquals(0.5f, acc[0], 0.08f)
        assertEquals(0.3f, acc[1], 0.08f)
        assertEquals(0.2f, acc[2], 0.08f)
    }

    @Test fun paletteChangeReachesTheTargetColor() {
        var palette by mutableStateOf(AuraPalette.of(listOf(Color.Red)))
        setAura {
            Aura(
                palette, Modifier.fillMaxSize(), shape = AuraShape.Fill,
                animation = AuraAnimation.None, colorTransition = tween(300),
            )
        }
        palette = AuraPalette.of(listOf(Color.Blue))
        rule.waitForIdle()
        val c = center()
        assertTrue(c.blue > 0.95f && c.red < 0.05f)
    }

    @Test fun interruptedTransitionStillEndsAtTheLastTarget() {
        var palette by mutableStateOf(AuraPalette.of(listOf(Color.Red)))
        setAura {
            Aura(
                palette, Modifier.fillMaxSize(), shape = AuraShape.Fill,
                animation = AuraAnimation.None, colorTransition = tween(300),
            )
        }
        rule.mainClock.autoAdvance = false
        palette = AuraPalette.of(listOf(Color.Blue))
        rule.mainClock.advanceTimeBy(150)
        palette = AuraPalette.of(listOf(Color.Green))
        rule.mainClock.advanceTimeBy(100)
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        assertTrue(center().green > 0.95f)
    }
}
