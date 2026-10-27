package io.github.pearwhitemb.aura.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.pearwhitemb.aura.sample.ui.theme.AuraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scenario = intent.getStringExtra("scenario")
            ?.let { runCatching { Scenario.valueOf(it) }.getOrNull() }
        setContent {
            AuraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (scenario != null) BenchmarkScreen(scenario) else AuraPlayground(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
