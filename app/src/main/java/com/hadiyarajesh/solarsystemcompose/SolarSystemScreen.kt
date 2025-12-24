package com.hadiyarajesh.solarsystemcompose

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

data class Planet(
    val name: String,
    val color: Color,
    val radius: Float,
    val orbitalRadius: Float,
    val orbitalVelocity: Float,
    val hasRings: Boolean = false,
    val hasRajesh: Boolean = false
)

@Composable
fun SolarSystemScreen() {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "SolarSystemTransition")
    
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

    val waveState = infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveState"
    )

    val floatingTitleOffset = infiniteTransition.animateFloat(
//        initialValue = 250f,
//        targetValue = 270f,
        initialValue = -100f,
        targetValue = -90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatingTitleState"
    )
    val altitudeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f, // Normalized altitude (0.0 to 1.0)
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 15000 // Slower overall
                0f at 0
                0f at 4000 // Longer wait on Earth
                1f at 9000 // Slower ascent
                1f at 11000 // Longer float at peak
                0f at 15000 // Slower descent
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "JumpState"
    )

    // Initial delay for Rajesh appearance
    var isRajeshActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2.seconds)
        isRajeshActive = true
    }

    // Track previous altitude to determine direction
    var prevAltitude by remember { mutableFloatStateOf(0f) }
    val isDescending = altitudeState.value < prevAltitude
    SideEffect {
        prevAltitude = altitudeState.value
    }

    val planets = listOf(
        Planet("Mercury", Color(0xFFBDBDBD), 8f, 100f, 8.0f),
        Planet("Venus", Color(0xFFE6BE8A), 14f, 150f, 6.0f),
        Planet("Earth", Color(0xFF2196F3), 15f, 210f, 4.5f, hasRajesh = true),
        Planet("Mars", Color(0xFFD32F2F), 12f, 270f, 3.5f),
        Planet("Jupiter", Color(0xFFFFA000), 36f, 380f, 2.0f),
        Planet("Saturn", Color(0xFFFDD835), 30f, 490f, 1.5f, hasRings = true),
        Planet("Uranus", Color(0xFF00ACC1), 22f, 580f, 1.1f),
        Planet("Neptune", Color(0xFF1976D2), 20f, 660f, 0.8f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050510))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val scaleFactor = (size.minDimension / 1500f) // Dynamic scaling to fit frame
            
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
                center.copy(y = center.y - 70f),
                textMeasurer,
                Color.White,
                1.0f
            )

            // Draw ComposeVerse Title
            drawComposeVerseTitle(floatingTitleOffset.value, textMeasurer)

            planets.forEach { planet ->
                val angle = Math.toRadians((rotationState.value * planet.orbitalVelocity).toDouble())
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
                    style = Stroke(width = 0.5.dp.toPx())
                )

                // Draw Rings for Saturn
                if (planet.hasRings) {
                    drawOval(
                        color = planet.color.copy(alpha = 0.4f),
                        topLeft = Offset(planetPosition.x - scaledPlanetRadius * 2.2f, planetPosition.y - scaledPlanetRadius * 0.8f),
                        size = Size(scaledPlanetRadius * 4.4f, scaledPlanetRadius * 1.6f),
                        style = Stroke(width = 4f * scaleFactor)
                    )
                }

                // Draw Planet with 3D spherical gradient
                val lightDirection = (center - planetPosition).run {
                    val dist = kotlin.math.sqrt(x*x + y*y)
                    if (dist != 0f) Offset(x / dist, y / dist) else Offset.Zero
                }
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(planet.color.copy(alpha = 1f), planet.color.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.8f)),
                        center = planetPosition + Offset(lightDirection.x * scaledPlanetRadius * 0.3f, lightDirection.y * scaledPlanetRadius * 0.3f),
                        radius = scaledPlanetRadius
                    ),
                    radius = scaledPlanetRadius,
                    center = planetPosition
                )

                // Draw Rajesh on Earth
                if (planet.hasRajesh && isRajeshActive) {
                    // radial direction from center
                    val dx = x - center.x
                    val dy = y - center.y
                    val dist = kotlin.math.sqrt(dx*dx + dy*dy)
                    val radialDir = if (dist != 0f) Offset(dx / dist, dy / dist) else Offset.Zero
                    
                    val jumpPeak = (660f - 210f) * scaleFactor // Restored full trajectory (Jump from Earth to Neptune)
                    val currentAltitude = altitudeState.value * jumpPeak

                    val rajeshPos = planetPosition + Offset(radialDir.x * (scaledPlanetRadius + currentAltitude), radialDir.y * (scaledPlanetRadius + currentAltitude))
                    drawRajesh(rajeshPos, waveState.value, altitudeState.value, isDescending, scaleFactor, textMeasurer)
                }

                // Draw Planet Name
                val labelY = if (planet.hasRajesh) {
                    planetPosition.y + scaledPlanetRadius + 15f * scaleFactor
                } else {
                    planetPosition.y - scaledPlanetRadius - 20f * scaleFactor
                }
                drawTextCentered(
                    planet.name,
                    planetPosition.copy(y = labelY),
                    textMeasurer,
                    Color.White.copy(alpha = 0.8f),
                    scaleFactor
                )
            }
        }
    }
}

private fun DrawScope.drawComposeVerseTitle(
    yOffset: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val textToDraw = "ComposeVerse"
    val textLayoutResult = textMeasurer.measure(
        text = textToDraw,
        style = TextStyle(
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            letterSpacing = 4.sp
        )
    )

    val titlePos = Offset(
        size.width / 2 - textLayoutResult.size.width / 2,
        80f + yOffset
    )

    // Draw Glow
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = titlePos,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF00B0FF), Color(0xFF6200EA), Color(0xFF00B0FF))
        ),
        alpha = 0.5f
    )

    // Draw Main Text
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = titlePos,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF80D8FF), Color(0xFFB388FF))
        )
    )
}

private fun DrawScope.drawRajesh(
    position: Offset,
    waveAngle: Float,
    altitudePercent: Float,
    isDescending: Boolean,
    scale: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // Grow size up to 4x as he jumps (base 1f + 3f at peak)
    val size = 15f * scale * (1f + altitudePercent * 3f)
    // Colors for Rajesh
    val shirtColor = Color(0xFFF44336) // Red
    val pantColor = Color(0xFF2196F3)  // Blue
    val skinColor = Color(0xFFFFCCBC)  // Skin tone

    // Draw Body (Shirt)
    drawLine(
        color = shirtColor,
        start = position + Offset(0f, -size * 0.4f),
        end = position + Offset(0f, -size),
        strokeWidth = 3f
    )
    // Draw Legs (Pants)
    drawLine(
        color = pantColor,
        start = position,
        end = position + Offset(0f, -size * 0.4f),
        strokeWidth = 3f
    )

    // Draw Head
    drawCircle(
        color = skinColor,
        radius = 4f,
        center = position + Offset(0f, -size - 4f)
    )
    // Draw Arms
    // Standing arm
    drawLine(
        color = skinColor,
        start = position + Offset(0f, -size * 0.7f),
        end = position + Offset(-8f, -size * 0.4f),
        strokeWidth = 2f
    )
    // Waving arm
    withTransform({
        rotate(waveAngle, position + Offset(0f, -size * 0.7f))
    }) {
        drawLine(
            color = skinColor,
            start = position + Offset(0f, -size * 0.7f),
            end = position + Offset(10f, -size),
            strokeWidth = 2f
        )
    }

    // Draw Rocket Booster if jumping
    if (altitudePercent > 0.01f) {
        val flameColor = Color(0xFFFF5722)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(flameColor, Color.Yellow, Color.Transparent),
                center = position,
                radius = 12f * (altitudePercent + 0.5f) * scale
            ),
            radius = 15f * (altitudePercent + 0.5f) * scale,
            center = position
        )
    }

    // Draw Fancy Speech Bubble
    val textToDraw = when {
        altitudePercent < 0.01f -> if (isDescending) "Safe landing! 🏡" else "Hi from Rajesh"
        isDescending -> "Brace for impact! ☄️"
        altitudePercent < 0.2f -> "Ignition! 🚀"
        altitudePercent < 0.5f -> "Woohooo! We're jumping! 🎢"
        altitudePercent < 0.8f -> "I can see the Sun! ☀️"
        altitudePercent < 0.98f -> "Neptune, here I come! 🌌"
        else -> "COMPOSE POWEEEERRR! ✨"
    }
    val textLayoutResult = textMeasurer.measure(
        text = textToDraw,
        style = TextStyle(fontSize = (9f * scale).sp)
    )
    val bubbleWidth = textLayoutResult.size.width + 12f * scale
    val bubbleHeight = textLayoutResult.size.height + 4f * scale
    
    val bubbleOffset = Offset(10f * scale, -size - 15f * scale)
    val bubblePos = position + bubbleOffset
    
    val bubblePath = Path().apply {
        moveTo(bubblePos.x, bubblePos.y)
        lineTo(bubblePos.x + bubbleWidth, bubblePos.y)
        lineTo(bubblePos.x + bubbleWidth, bubblePos.y - bubbleHeight)
        lineTo(bubblePos.x, bubblePos.y - bubbleHeight)
        close()
        // Pointer
        moveTo(bubblePos.x + 5f, bubblePos.y)
        lineTo(bubblePos.x - 5f, bubblePos.y + 10f)
        lineTo(bubblePos.x + 15f, bubblePos.y)
    }
    
    // Fancy bubble background
    drawPath(
        path = bubblePath,
        brush = Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF5F5F5))
        )
    )
    // Bubble border
    drawPath(
        path = bubblePath,
        color = Color(0xFFFFD600),
        style = Stroke(width = 1f)
    )
    
    // Fancy Colorful Text
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(bubblePos.x + 6f, bubblePos.y - bubbleHeight + 2f),
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5), Color(0xFF00BCD4))
        )
    )
}

private fun DrawScope.drawStars(twinkle: Float) {
    val random = java.util.Random(42)
    repeat(200) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val isTwinkling = it % 3 == 0
        val baseAlpha = random.nextFloat() * 0.5f + 0.2f
        val alpha = if (isTwinkling) baseAlpha * twinkle else baseAlpha
        
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = random.nextFloat() * 1.5f + 0.5f,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawTextCentered(
    text: String,
    position: Offset,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    color: Color,
    scale: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(color = color, fontSize = (10f * scale).sp)
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(
            position.x - textLayoutResult.size.width / 2,
            position.y - textLayoutResult.size.height / 2
        )
    )
}

@Preview
@Composable
fun SolarSystemPreview() {
    SolarSystemScreen()
}
