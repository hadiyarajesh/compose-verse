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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.cos
import kotlin.math.roundToInt
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
                durationMillis = 20000 // Balanced 20s cycle
                0.01f at 0 // Show "Hi from Rajesh" to start
                0.01f at 3000 // Hold greeting for 3s
                0.02f at 3001 // Ignition!
                1f at 8000 // Reaches Peak
                1f at 10000 // Floats at Peak
                0.01f at 16000 // Lands
                0.009f at 18500 // Holds "Safe landing!" for 2.5s
                0f at 18501 // Disappears
                0f at 20000 // Stay hidden until restart
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "JumpState"
    )

    // Initial delay for Rajesh appearance
    var isRajeshActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((2).seconds)
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
                center.copy(y = center.y - 70f * scaleFactor),
                textMeasurer,
                Color.White,
                scaleFactor
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
                if (planet.hasRajesh && isRajeshActive && altitudeState.value > 0f) {
                    // radial direction from center
                    val dx = x - center.x
                    val dy = y - center.y
                    val dist = kotlin.math.sqrt(dx*dx + dy*dy)
                    val radialDir = if (dist != 0f) Offset(dx / dist, dy / dist) else Offset.Zero
                    
                    val jumpPeak = (660f - 210f) * scaleFactor // Restored full trajectory (Jump from Earth to Neptune)
                    val currentAltitude = altitudeState.value * jumpPeak

                    val rajeshPos = planetPosition + Offset(radialDir.x * (scaledPlanetRadius + currentAltitude), radialDir.y * (scaledPlanetRadius + currentAltitude))
                    drawRajesh(rajeshPos, waveState.value, altitudeState.value, isDescending, scaleFactor, textMeasurer, rajeshImage)
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
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    rajeshImage: ImageBitmap
) {
    // Base size increased 3x (from 15f to 45f). Grows up to 4x total at peak.
    val rajeshSize = 30f * scale * (1f + altitudePercent * 2f)
    
    // Draw Advanced Rocket
    val rocketWidth = rajeshSize * 0.9f
    val rocketHeight = rajeshSize * 1.8f
    
    withTransform({
        rotate(waveAngle / 2f, position)
    }) {
        // 1. Draw Exhaust Plume (only when moving)
        if (altitudePercent > 0.01f) {
            val flicker = (waveAngle % 5f) * scale
            val plumePath = Path().apply {
                moveTo(position.x - rocketWidth * 0.3f, position.y)
                quadraticTo(position.x, position.y + rocketHeight * 0.8f + flicker, position.x + rocketWidth * 0.3f, position.y)
                close()
            }
            drawPath(
                path = plumePath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEA00), Color(0xFFFF5722), Color.Transparent),
                    startY = position.y,
                    endY = position.y + rocketHeight * 0.8f + flicker
                ),
                alpha = 0.8f
            )
            
            // Core bloom
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = rocketWidth * 0.2f,
                center = position + Offset(0f, 5f * scale)
            )
        }

        // 2. Rocket Fins (Rear)
        val finPath = Path().apply {
            // Left Fin
            moveTo(position.x - rocketWidth * 0.45f, position.y - rocketHeight * 0.4f)
            lineTo(position.x - rocketWidth * 0.9f, position.y)
            lineTo(position.x - rocketWidth * 0.45f, position.y)
            close()
            // Right Fin
            moveTo(position.x + rocketWidth * 0.45f, position.y - rocketHeight * 0.4f)
            lineTo(position.x + rocketWidth * 0.9f, position.y)
            lineTo(position.x + rocketWidth * 0.45f, position.y)
            close()
        }
        drawPath(finPath, Color(0xFFC62828)) // Darker Red for fins

        // 3. Rocket Body (Main Cylinder + Nose)
        val bodyPath = Path().apply {
            moveTo(position.x, position.y - rocketHeight) // Tip
            // Nose Cone curve
            cubicTo(
                position.x + rocketWidth * 0.5f, position.y - rocketHeight,
                position.x + rocketWidth * 0.5f, position.y - rocketHeight * 0.6f,
                position.x + rocketWidth * 0.5f, position.y - rocketHeight * 0.3f
            )
            // Body line
            lineTo(position.x + rocketWidth * 0.5f, position.y) 
            lineTo(position.x - rocketWidth * 0.5f, position.y)
            lineTo(position.x - rocketWidth * 0.5f, position.y - rocketHeight * 0.3f)
            // Other nose curve
            cubicTo(
                position.x - rocketWidth * 0.5f, position.y - rocketHeight * 0.6f,
                position.x - rocketWidth * 0.5f, position.y - rocketHeight,
                position.x, position.y - rocketHeight
            )
            close()
        }
        
        drawPath(
            path = bodyPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF5F5F5), Color(0xFFBDBDBD), Color(0xFF757575)),
                start = Offset(position.x - rocketWidth, position.y),
                end = Offset(position.x + rocketWidth, position.y)
            )
        )

        // 4. Panel Details & Rivets
        val detailColor = Color.Black.copy(alpha = 0.2f)
        // Horizontal panel line
        drawLine(
            color = detailColor,
            start = Offset(position.x - rocketWidth * 0.5f, position.y - rocketHeight * 0.35f),
            end = Offset(position.x + rocketWidth * 0.5f, position.y - rocketHeight * 0.35f),
            strokeWidth = 1f * scale
        )
        // Rivets
        for (i in -2..2) {
            drawCircle(
                color = detailColor,
                radius = 1.5f * scale,
                center = Offset(position.x + (i * rocketWidth * 0.2f), position.y - 10f * scale)
            )
        }

        // 5. Cockpit / Window
        val windowSize = rocketWidth * 0.8f
        val windowCenter = position + Offset(0f, -rocketHeight * 0.6f)
        
        // Window Frame
        drawCircle(
            color = Color(0xFF455A64),
            radius = windowSize * 0.55f,
            center = windowCenter
        )
        // Glass
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFB3E5FC), Color(0xFF03A9F4)),
                center = windowCenter - Offset(windowSize * 0.1f, windowSize * 0.1f),
                radius = windowSize * 0.5f
            ),
            radius = windowSize * 0.5f,
            center = windowCenter
        )

        // 6. Rajesh (In the window with circular clipping)
        val imageSize = windowSize * 0.95f
        val imageOffset = windowCenter - Offset(imageSize / 2, imageSize / 2)
        
        withTransform({
            clipPath(Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(windowCenter, windowSize * 0.5f))
            })
        }) {
            drawImage(
                image = rajeshImage,
                dstOffset = IntOffset(
                    (imageOffset.x).roundToInt(),
                    (imageOffset.y).roundToInt()
                ),
                dstSize = IntSize(
                    imageSize.roundToInt(),
                    imageSize.roundToInt()
                )
            )
        }
    }


    // Draw Fancy Speech Bubble
    val textToDraw = when {
        altitudePercent < 0.02f && isDescending -> "Safe landing! 🏡"
        altitudePercent < 0.02f -> "Hi from Rajesh" // Restored initial greeting
        isDescending -> "Brace for impact! ☄️"
        altitudePercent < 0.2f -> "Ignition! 🚀"
        altitudePercent < 0.6f -> "Woohooo! We're jumping! 🎢"
        altitudePercent < 0.98f -> "Neptune, here I come! 🌌"
        else -> "COMPOSE POWEEEERRR! ✨"
    }
    val textLayoutResult = textMeasurer.measure(
        text = textToDraw,
        style = TextStyle(fontSize = (9f * scale).sp)
    )
    val bubbleWidth = textLayoutResult.size.width + 12f * scale
    val bubbleHeight = textLayoutResult.size.height + 4f * scale
    
    val bubbleOffset = Offset(rajeshSize + 5f * scale, -rajeshSize - 20f * scale)
    val bubblePos = position + bubbleOffset
    
    val bubblePath = Path().apply {
        moveTo(bubblePos.x, bubblePos.y)
        lineTo(bubblePos.x + bubbleWidth, bubblePos.y)
        lineTo(bubblePos.x + bubbleWidth, bubblePos.y - bubbleHeight)
        lineTo(bubblePos.x, bubblePos.y - bubbleHeight)
        close()
        // Pointer - points back to head area
        moveTo(bubblePos.x + 2f * scale, bubblePos.y)
        lineTo(position.x + rajeshSize * 0.5f, position.y - rajeshSize * 1.2f)
        lineTo(bubblePos.x + 12f * scale, bubblePos.y)
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
