package io.github.pearwhitemb.aura

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PaletteBlendTest {
    private val a = AuraPalette.weighted(Color.Red to 0.7f, Color.Blue to 0.3f)
    private val b = AuraPalette.weighted(Color.Green to 0.4f, Color.Yellow to 0.6f)

    private fun PaletteFrame.color(i: Int) =
        Color(colors[i * 4], colors[i * 4 + 1], colors[i * 4 + 2], colors[i * 4 + 3])

    private fun assertColor(expected: Color, actual: Color) {
        assertEquals(expected.red, actual.red, EPS)
        assertEquals(expected.green, actual.green, EPS)
        assertEquals(expected.blue, actual.blue, EPS)
    }

    @Test fun `fraction 0 reproduces the first palette`() {
        val f = PaletteFrame().also { blendInto(a, b, 0f, it) }
        assertEquals(2, f.count)
        assertEquals(0.7f, f.bounds[0], EPS)
        assertEquals(1f, f.bounds[1], EPS)
        assertColor(Color.Red, f.color(0))
        assertColor(Color.Blue, f.color(1))
    }

    @Test fun `fraction 1 reproduces the second palette`() {
        val f = PaletteFrame().also { blendInto(a, b, 1f, it) }
        assertEquals(0.4f, f.bounds[0], EPS)
        assertEquals(1f, f.bounds[1], EPS)
        assertColor(Color.Green, f.color(0))
        assertColor(Color.Yellow, f.color(1))
    }

    @Test fun `weights are interpolated`() {
        val f = PaletteFrame().also { blendInto(a, b, 0.5f, it) }
        assertEquals(0.55f, f.bounds[0], EPS)
        assertEquals(1f, f.bounds[1], EPS)
    }

    @Test
    fun `bounds never decrease and always end at 1`() {
        val f = PaletteFrame()
        for (step in 0..10) {
            blendInto(a, b, step / 10f, f)
            for (i in 1 until f.count) assertTrue(f.bounds[i] >= f.bounds[i - 1])
            assertEquals(1f, f.bounds[f.count - 1], EPS)
        }
    }

    @Test fun `shrinking palette pads with zero weight copies of the last color`() {
        val three = AuraPalette.of(listOf(Color.Red, Color.Green, Color.Blue))
        val one = AuraPalette.of(listOf(Color.Yellow))
        val f = PaletteFrame()

        blendInto(three, one, 0f, f)
        assertEquals(3, f.count)
        assertEquals(1f / 3, f.bounds[0], EPS)

        blendInto(three, one, 1f, f)
        assertEquals(3, f.count)
        assertEquals(listOf(1f, 1f, 1f), List(3) { f.bounds[it] }.map { Math.round(it * 1000f) / 1000f })
        assertColor(Color.Yellow, f.color(1))
        assertColor(Color.Yellow, f.color(2))
    }

    @Test fun `growing palette starts with zero weight entries`() {
        val one = AuraPalette.of(listOf(Color.Yellow))
        val three = AuraPalette.of(listOf(Color.Red, Color.Green, Color.Blue))
        val f = PaletteFrame().also { blendInto(one, three, 0f, it) }
        assertEquals(3, f.count)
        assertEquals(1f, f.bounds[0], EPS)
        assertEquals(1f, f.bounds[1], EPS)
        assertColor(Color.Yellow, f.color(2))
    }

    @Test fun `snapshot palette matches the frame at the same fraction`() {
        val snapshot = blend(a, b, 0.3f)
        val f = PaletteFrame().also { blendInto(a, b, 0.3f, it) }
        assertEquals(1f, snapshot.weights.sum(), EPS)
        var acc = 0f
        for (i in 0 until f.count) {
            acc += snapshot.weights[i]
            assertEquals(f.bounds[i], acc, EPS)
        }
    }

    private companion object { const val EPS = 1e-5f }
}
