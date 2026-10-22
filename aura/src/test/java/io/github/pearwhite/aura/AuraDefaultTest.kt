package io.github.pearwhite.aura

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.random.Random

class AuraDefaultsTest {
    @Test fun `vibrantPalette size is clamped`() {
        assertEquals(1, AuraDefaults.vibrantPalette(0).size)
        assertEquals(AuraDefaults.MaxColors, AuraDefaults.vibrantPalette(99).size)
        assertEquals(4, AuraDefaults.vibrantPalette().size)
    }

    @Test fun `vibrantPalette is deterministic for a seeded Random`() {
        assertEquals(
            AuraDefaults.vibrantPalette(6, Random(42)),
            AuraDefaults.vibrantPalette(6, Random(42)),
        )
    }

    @Test fun `vibrantPalette colors are saturated and bright`() {
        AuraDefaults.vibrantPalette(AuraDefaults.MaxColors, Random(1)).repeat(25).forEach { c ->
            val max = maxOf(c.red, c.green, c.blue)
            val min = minOf(c.red, c.green, c.blue)
            assertTrue("value ${max}", max >= 0.85f - TOL)
            assertTrue("saturation ${(max - min) / max}", (max - min) / max >= 0.8f - TOL)
        }
    }

    private fun List<Color>.repeat(n: Int) = List(n) { this }.flatten()
    private companion object { const val TOL = 0.01f }
}
