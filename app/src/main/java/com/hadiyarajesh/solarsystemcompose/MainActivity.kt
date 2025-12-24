package com.hadiyarajesh.solarsystemcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hadiyarajesh.solarsystemcompose.ui.ComposeVerseScreen
import com.hadiyarajesh.solarsystemcompose.ui.theme.SolarSystemComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarSystemComposeTheme {
                ComposeVerseScreen()
            }
        }
    }
}
