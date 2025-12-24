package com.hadiyarajesh.solarsystemcompose.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

/**
 * Calculates a normalized direction vector from [source] to [destination].
 * Returns [Offset.Zero] if sources are the same.
 */
fun calculateDirectionUnit(source: Offset, destination: Offset): Offset {
    val dx = destination.x - source.x
    val dy = destination.y - source.y
    val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    return if (dist != 0f) Offset(dx / dist, dy / dist) else Offset.Zero
}
