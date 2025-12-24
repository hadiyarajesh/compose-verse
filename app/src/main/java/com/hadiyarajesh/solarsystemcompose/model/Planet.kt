package com.hadiyarajesh.solarsystemcompose.model

import androidx.compose.ui.graphics.Color

enum class PlanetName(val displayName: String) {
    Mercury("Mercury"),
    Venus("Venus"),
    Earth("Earth"),
    Mars("Mars"),
    Jupiter("Jupiter"),
    Saturn("Saturn"),
    Uranus("Uranus"),
    Neptune("Neptune")
}

data class Planet(
    val name: PlanetName,
    val color: Color,
    val radius: Float,
    val orbitalRadius: Float,
    val orbitalVelocity: Float,
    val hasRings: Boolean = false,
    val hasRajesh: Boolean = false
)
