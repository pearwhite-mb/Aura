package io.github.pearwhitemb.benchmarks

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuraFrameBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test fun blobSmall() = measure("BlobSmall")
    @Test fun fillFullScreen() = measure("FillFullScreen")
    @Test fun fillEightColorsDetailed() = measure("FillEightColorsDetailed")
    @Test fun roundedRectCard() = measure("RoundedRectCard")
    @Test fun paletteSwitching() = measure("PaletteSwitching")

    @Test fun fillFullScreen60Fps() = measure("FillFullScreen60Fps")
    @Test fun fillFullScreen30Fps() = measure("FillFullScreen30Fps")
    @Test fun fillFullScreen15Fps() = measure("FillFullScreen15Fps")

    private fun measure(scenario: String) = rule.measureRepeated(
        packageName = "io.github.pearwhitemb.aura.sample",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        compilationMode = CompilationMode.Full(),
        setupBlock = {
            pressHome()
            startActivityAndWait { it.putExtra("scenario", scenario) }
            Thread.sleep(1000)
        },
    ) {
        Thread.sleep(10_000)
    }
}
