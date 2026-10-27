package io.github.pearwhitemb.aura

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SdkSuppress(minSdkVersion = 33)
@RunWith(AndroidJUnit4::class)
class AuraLifecycleTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun animationStopsWhenLifecycleDropsBelowStarted() {
        val owner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        rule.mainClock.autoAdvance = false
        rule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                Box(Modifier.size(100.dp).background(Color.Black).testTag("area")) {
                    Aura(
                        AuraPalette.of(listOf(Color.Red, Color.Green, Color.Blue)),
                        Modifier.fillMaxSize(), shape = AuraShape.Fill,
                        animation = AuraAnimation.Flow(speed = 3f),
                    )
                }
            }
        }
        fun frame() = rule.onNodeWithTag("area").captureToImage().toPixelMap().buffer.copyOf()

        rule.mainClock.advanceTimeBy(500)
        val running1 = frame()
        rule.mainClock.advanceTimeBy(500)
        assertFalse("animation should be running", running1.contentEquals(frame()))

        rule.runOnUiThread { owner.currentState = Lifecycle.State.CREATED }
        rule.mainClock.advanceTimeBy(100)
        val paused1 = frame()
        rule.mainClock.advanceTimeBy(1000)
        val paused2 = frame()
        assertTrue("frame should remain unchanged while paused", paused1.contentEquals(paused2))

        rule.runOnUiThread { owner.currentState = Lifecycle.State.RESUMED }
        rule.mainClock.advanceTimeBy(1000)
        assertFalse("animation should resume after returning", paused2.contentEquals(frame()))
    }
}
