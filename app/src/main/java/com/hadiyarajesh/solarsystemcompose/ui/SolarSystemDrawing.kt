package com.hadiyarajesh.solarsystemcompose.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.random.Random

fun DrawScope.drawComposeVerseTitle(
    yOffset: Float,
    textMeasurer: TextMeasurer,
    scaleFactor: Float
) {
    val isLandscape = size.width > size.height
    val dynamicFontSize = (if (isLandscape) 28f else 36f) * scaleFactor

    val textToDraw = "ComposeVerse"
    val textLayoutResult = textMeasurer.measure(
        text = textToDraw,
        style = TextStyle(
            fontSize = dynamicFontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (if (isLandscape) 3f else 6f).sp
        )
    )

    val titlePos = Offset(
        if (isLandscape) 40f * scaleFactor else (size.width / 2 - textLayoutResult.size.width / 2),
        (if (isLandscape) 120f else 180f) * scaleFactor + yOffset
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

fun DrawScope.drawRajesh(
    position: Offset,
    waveAngle: Float,
    altitudePercent: Float,
    isDescending: Boolean,
    scale: Float,
    textMeasurer: TextMeasurer,
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
                quadraticTo(
                    position.x,
                    position.y + rocketHeight * 0.8f + flicker,
                    position.x + rocketWidth * 0.3f,
                    position.y
                )
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
                addOval(Rect(windowCenter, windowSize * 0.5f))
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

    // --- Near Miss Shooting Star (From Deep Space) ---
    if (altitudePercent > 0.3f && altitudePercent < 0.7f && !isDescending) {
        val sProgress = (altitudePercent - 0.3f) / 0.4f

        // Start far off-screen and end far off-screen
        val startOffset = Offset(position.x + 1000f * scale, position.y - 500f * scale)
        val endOffset = Offset(position.x - 1000f * scale, position.y + 500f * scale)

        val currentX = startOffset.x + (endOffset.x - startOffset.x) * sProgress
        val currentY = startOffset.y + (endOffset.y - startOffset.y) * sProgress

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, Color.White),
                start = Offset(currentX + 80f * scale, currentY - 40f * scale),
                end = Offset(currentX, currentY)
            ),
            start = Offset(currentX + 80f * scale, currentY - 40f * scale),
            end = Offset(currentX, currentY),
            strokeWidth = 3f * scale
        )
        drawCircle(
            color = Color.White,
            radius = 3.5f * scale,
            center = Offset(currentX, currentY)
        )
    }

    // Draw Fancy Speech Bubble
    val textToDraw = when {
        // High Priority Transitions
        isDescending && altitudePercent < 0.02f -> "Safe landing! 🏡"
        !isDescending && altitudePercent < 0.05f -> "Hi from Rajesh"

        // Neptune focus (Ascent & Early Descent)
        !isDescending && altitudePercent >= 0.70f -> "Neptune, here I come! 🌌"
        isDescending && altitudePercent >= 0.85f -> "Neptune, here I come! 🌌"

        // Ascent Specials
        !isDescending && altitudePercent >= 0.50f -> "WHOA! That was close! ☄️💨"
        !isDescending && altitudePercent >= 0.20f -> "Woohooo! We're jumping! 🎢"
        !isDescending -> "Ignition! 🚀"

        // Descent Specials
        isDescending && altitudePercent >= 0.40f -> "COMPOSE POWEEEERRR! ✨"
        else -> "Brace for impact! ☄️"
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
            colors = listOf(
                Color(0xFFE91E63),
                Color(0xFF9C27B0),
                Color(0xFF3F51B5),
                Color(0xFF00BCD4)
            )
        )
    )
}

fun DrawScope.drawNebulae(alpha: Float) {
    val random = Random(99)
    repeat(3) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val radius = 300f + random.nextFloat() * 200f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    listOf(Color(0xFF311B92), Color(0xFF006064), Color(0xFF1B5E20)).random(random)
                        .copy(alpha = alpha),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = radius
            ),
            radius = radius,
            center = Offset(x, y)
        )
    }
}

fun DrawScope.drawShootingStar(progress: Float) {
    if (progress <= 0f || progress >= 1f) return

    val startX = size.width * 1.2f
    val startY = size.height * 0.2f
    val endX = -size.width * 0.2f
    val endY = size.height * 0.8f

    val currentX = startX + (endX - startX) * progress
    val currentY = startY + (endY - startY) * progress

    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.White),
            start = Offset(currentX + 50f, currentY - 20f),
            end = Offset(currentX, currentY)
        ),
        start = Offset(currentX + 50f, currentY - 20f),
        end = Offset(currentX, currentY),
        strokeWidth = 2f
    )

    drawCircle(
        color = Color.White,
        radius = 2f,
        center = Offset(currentX, currentY)
    )
}

fun DrawScope.drawStars(twinkle: Float) {
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

fun DrawScope.drawTextCentered(
    text: String,
    position: Offset,
    textMeasurer: TextMeasurer,
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
