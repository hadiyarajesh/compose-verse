package com.hadiyarajesh.solarsystemcompose.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import com.hadiyarajesh.solarsystemcompose.R
import com.hadiyarajesh.solarsystemcompose.model.PlanetName
import com.hadiyarajesh.solarsystemcompose.util.HapticHelper
import com.hadiyarajesh.solarsystemcompose.util.calculateDirectionUnit
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

@Composable
fun ComposeVerseScreen() {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "SolarSystemTransition")
    val rajeshImage = ImageBitmap.imageResource(id = R.drawable.rajesh)

    val rotationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationState"
    )

    val twinkleState = infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TwinkleState"
    )

    // Shooting Star State
    val shootingStarProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 10000 // 10s cycle
                0f at 0
                0f at 4000 // Wait 4s
                1f at 7000 // Slower streak (3s)
                1f at 10000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ShootingStarState"
    )

    // Nebula Pulse State
    val nebulaAlpha = infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "NebulaAlpha"
    )

    val waveState = infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveState"
    )

    val floatingTitleOffset = infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatingTitleState"
    )

    // Rajesh Jump State
    val altitudeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 20000
                0f at 0
                0f at 2000 // Initial delay/greeting wait (increased stay at 0)
                0.01f at 2500 // Ignition (starts at 2.5s)
                1.0f at 10000 // Peak at 10s
                0.01f at 15500 // Fast descent
                0.009f at 18000 // Landed, showing message
                0f at 18001 // Disappear
                0f at 20000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "JumpState"
    )

    // Initial delay for Rajesh appearance
    var isRajeshActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((0.5).seconds) // Reduced from 2s
        isRajeshActive = true
    }

    // Track previous altitude to determine direction
    var prevAltitude by remember { mutableFloatStateOf(0f) }
    val isDescending = altitudeState.value < prevAltitude
    SideEffect {
        prevAltitude = altitudeState.value
    }

    val context = LocalContext.current
    // Haptic Trigger for Ignition
    LaunchedEffect(altitudeState.value) {
        if (altitudeState.value > 0.01f && altitudeState.value < 0.1f && !isDescending) {
            HapticHelper.vibrate(context)
        }
    }

    // Audio Placeholder
    // In a real app, you would use MediaPlayer or ExoPlayer here to play cosmic ambient music.
    // LaunchedEffect(Unit) { /* start background music */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050510))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val scaleFactor = (size.minDimension / 1500f) // Dynamic scaling to fit frame

            // Draw Background Nebula
            drawNebulae(nebulaAlpha.value)

            // Draw Stars
            drawStars(twinkleState.value)

            // Draw Sun Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEA00), Color(0xFFFF9800), Color.Transparent),
                    center = center,
                    radius = 90f * scaleFactor
                ),
                radius = 90f * scaleFactor,
                center = center
            )

            // Draw Sun
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD600), Color(0xFFFF8F00)),
                    center = center,
                    radius = 40f * scaleFactor
                ),
                radius = 40f * scaleFactor,
                center = center
            )

            // Draw Sun Name
            drawTextCentered(
                "Sun",
                center.copy(y = center.y - 70f * scaleFactor),
                textMeasurer,
                Color.White,
                scaleFactor
            )

            // Draw ComposeVerse Title
            drawComposeVerseTitle(floatingTitleOffset.value, textMeasurer, scaleFactor)

            planets.forEach { planet ->
                val angle =
                    Math.toRadians((rotationState.value * planet.orbitalVelocity).toDouble())
                val scaledOrbitalRadius = planet.orbitalRadius * scaleFactor
                val x = center.x + cos(angle).toFloat() * scaledOrbitalRadius
                val y = center.y + sin(angle).toFloat() * scaledOrbitalRadius
                val planetPosition = Offset(x, y)
                val scaledPlanetRadius = planet.radius * scaleFactor

                // Draw Orbit
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = scaledOrbitalRadius,
                    center = center,
                    style = Stroke(width = 1f)
                )

                if (planet.name == PlanetName.Saturn) {
                    // Saturn's Rings
                    drawCircle(
                        color = Color(0xFFFDD835).copy(alpha = 0.3f),
                        radius = scaledPlanetRadius * 1.8f,
                        center = planetPosition,
                        style = Stroke(width = 8f * scaleFactor)
                    )
                }

                // Draw Planet Glow (Atmosphere)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(planet.color.copy(alpha = 0.3f), Color.Transparent),
                        center = planetPosition,
                        radius = scaledPlanetRadius * 1.5f
                    ),
                    radius = scaledPlanetRadius * 1.5f,
                    center = planetPosition
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            planet.color.copy(alpha = 1f),
                            planet.color.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        center = planetPosition + Offset(
                            scaledPlanetRadius * 0.3f,
                            scaledPlanetRadius * 0.3f
                        ),
                        radius = scaledPlanetRadius
                    ),
                    radius = scaledPlanetRadius,
                    center = planetPosition
                )

                drawTextCentered(
                    planet.name.displayName,
                    planetPosition.copy(y = planetPosition.y + scaledPlanetRadius + 15f * scaleFactor),
                    textMeasurer,
                    Color.White,
                    scaleFactor
                )

                // Draw Rajesh Rider on his cosmic jump!
                if (planet.hasRajesh && isRajeshActive && altitudeState.value > 0f) {
                    val altitudePercent = altitudeState.value
                    // Jump towards Neptune (normalized trajectory)
                    val peakAltitude = 450f * scaleFactor
                    val currentAltitude = peakAltitude * altitudePercent

                    // Direction vector from Sun to Earth
                    val dir = calculateDirectionUnit(center, planetPosition)

                    val rajeshPosition =
                        planetPosition + Offset(dir.x * currentAltitude, dir.y * currentAltitude)

                    drawRajesh(
                        position = rajeshPosition,
                        waveAngle = waveState.value,
                        altitudePercent = altitudePercent,
                        isDescending = isDescending,
                        scale = scaleFactor,
                        textMeasurer = textMeasurer,
                        rajeshImage = rajeshImage
                    )
                }
            }

            // Draw Shooting Stars in the background too
            drawShootingStar(shootingStarProgress.value)
        }
    }
}

@Preview
@Composable
fun SolarSystemPreview() {
    ComposeVerseScreen()
}
