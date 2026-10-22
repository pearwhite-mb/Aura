package io.github.pearwhite.aura

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AuraPaletteTest {
    private val red = Color.Red
    private val green = Color.Green
    private val blue = Color.Blue

    @Test fun `of gives equal weights`() {
        val p = AuraPalette.of(listOf(red, green, blue, Color.White))
        assertEquals(4, p.size)
        p.weights.forEach { assertEquals(0.25f, it, EPS) }
    }

    @Test fun `weights are normalized to sum 1`() {
        val p = AuraPalette.weighted(red to 3f, green to 1f)
        assertEquals(0.75f, p.weights[0], EPS)
        assertEquals(0.25f, p.weights[1], EPS)
        assertEquals(1f, p.weights.sum(), EPS)
    }

    @Test fun `counts can be passed as weights`() {
        val p = AuraPalette.weighted(red to 70f, blue to 30f)
        assertEquals(0.7f, p.weights[0], EPS)
        assertEquals(0.3f, p.weights[1], EPS)
    }

    @Test fun `negative NaN and infinite weights count as zero`() {
        val p = AuraPalette.weighted(
            red to -1f, green to Float.NaN, blue to Float.POSITIVE_INFINITY, Color.White to 2f,
        )
        assertEquals(listOf(0f, 0f, 0f, 1f), p.weights)
    }

    @Test fun `all zero weights fall back to equal shares`() {
        val p = AuraPalette.weighted(red to 0f, green to 0f)
        assertEquals(listOf(0.5f, 0.5f), p.weights)
    }

    @Test fun `zero weight entry in the middle is kept`() {
        val p = AuraPalette.weighted(red to 1f, green to 0f, blue to 1f)
        assertEquals(3, p.size)
        assertEquals(listOf(0.5f, 0f, 0.5f), p.weights)
    }

    @Test fun `entries beyond MaxColors are dropped`() {
        val p = AuraPalette.of(List(12) { Color.hsv(it * 30f, 1f, 1f) })
        assertEquals(AuraDefaults.MaxColors, p.size)
    }

    @Test fun `empty input gives Empty`() {
        val p = AuraPalette.weighted(emptyList<Pair<Color, Float>>())
        assertTrue(p.isEmpty)
        assertEquals(AuraPalette.Empty, p)
    }

    @Test fun `same content means equal palettes`() {
        val a = AuraPalette.of(listOf(red, green))
        val b = AuraPalette.weighted(red to 1f, green to 1f)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test fun `different weights mean different palettes`() {
        assertNotEquals(
            AuraPalette.weighted(red to 1f, green to 2f),
            AuraPalette.weighted(red to 1f, green to 1f),
        )
    }

    private companion object { const val EPS = 1e-5f }
}
