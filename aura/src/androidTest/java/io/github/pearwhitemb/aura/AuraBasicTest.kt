package io.github.pearwhitemb.aura

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuraBasicTest {
    @get:Rule val rule = createComposeRule()

    @Test fun emptyPaletteRendersNothing() {
        rule.setContent { Aura(AuraPalette.Empty, Modifier.testTag("aura").size(100.dp)) }
        rule.onNodeWithTag("aura").assertDoesNotExist()
    }

    @Test fun centerIsPaintedOnAnyApiLevel() {
        rule.setContent {
            Box(Modifier.size(200.dp).background(Color.Black).testTag("area")) {
                Aura(AuraPalette.of(listOf(Color.Red)), Modifier.fillMaxSize(), animation = AuraAnimation.None)
            }
        }
        val px = rule.onNodeWithTag("area").captureToImage().toPixelMap()
        assertTrue(px[px.width / 2, px.height / 2].red > 0.9f)
    }

    @Test fun zeroSizeDoesNotCrash() {
        rule.setContent {
            Aura(
                AuraPalette.of(listOf(Color.Red, Color.Blue)),
                Modifier.size(0.dp),
                animation = AuraAnimation.None,
            )
        }
        rule.waitForIdle()
    }

    @Test fun zeroSizeWithAnimationDoesNotCrash() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            Aura(AuraPalette.of(listOf(Color.Red, Color.Blue)), Modifier.size(0.dp))
        }
        rule.mainClock.advanceTimeBy(500)
    }
}
