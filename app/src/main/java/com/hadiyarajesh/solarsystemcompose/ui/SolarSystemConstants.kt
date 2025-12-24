package com.hadiyarajesh.solarsystemcompose.ui

import androidx.compose.ui.graphics.Color
import com.hadiyarajesh.solarsystemcompose.model.Planet
import com.hadiyarajesh.solarsystemcompose.model.PlanetName

val planets = listOf(
    Planet(PlanetName.Mercury, Color(0xFFBDBDBD), 8f, 100f, 8.0f),
    Planet(PlanetName.Venus, Color(0xFFE6BE8A), 14f, 150f, 6.0f),
    Planet(PlanetName.Earth, Color(0xFF2196F3), 15f, 210f, 4.5f, hasRajesh = true),
    Planet(PlanetName.Mars, Color(0xFFD32F2F), 12f, 270f, 3.5f),
    Planet(PlanetName.Jupiter, Color(0xFFFFA000), 36f, 380f, 2.0f),
    Planet(PlanetName.Saturn, Color(0xFFFDD835), 30f, 490f, 1.5f, hasRings = true),
    Planet(PlanetName.Uranus, Color(0xFF00ACC1), 22f, 580f, 1.1f),
    Planet(PlanetName.Neptune, Color(0xFF1976D2), 20f, 660f, 0.8f)
)
