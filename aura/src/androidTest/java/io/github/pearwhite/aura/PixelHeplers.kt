package io.github.pearwhite.aura

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap

fun PixelMap.shares(palette: List<Color>, step: Int = 4): FloatArray {
    val counts = IntArray(palette.size)
    var total = 0
    for (y in 0 until height step step) {
        for (x in 0 until width step step) {
            val c = this[x, y]
            var best = 0
            var bestD = Float.MAX_VALUE
            palette.forEachIndexed { i, p ->
                val dr = c.red - p.red
                val dg = c.green - p.green
                val db = c.blue - p.blue
                val d = dr * dr + dg * dg + db * db
                if (d < bestD) { bestD = d; best = i }
            }
            counts[best]++
            total++
        }
    }
    return FloatArray(palette.size) { counts[it].toFloat() / total }
}
