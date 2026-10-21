package io.github.pearwhite.aura.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.pearwhite.aura.Aura
import io.github.pearwhite.aura.AuraDefaults
import io.github.pearwhite.aura.sample.ui.theme.AuraTheme
import kotlin.random.Random

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
    var colors by remember { mutableStateOf(AuraDefaults.vibrantPalette(4)) }
    var seed by remember { mutableFloatStateOf(Random.nextFloat() * 100f) }

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Aura(
            colors = colors,
            seed = seed,
            modifier = Modifier.size(360.dp)
        )
        Spacer(Modifier.height(48.dp))
        Button(onClick = {
            colors = AuraDefaults.vibrantPalette(Random.nextInt(2, 9))
            seed = Random.nextFloat() * 100f
        }) { Text("Generate Aura") }
    }
}
