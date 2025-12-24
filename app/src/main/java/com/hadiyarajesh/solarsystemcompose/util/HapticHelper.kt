package com.hadiyarajesh.solarsystemcompose.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object HapticHelper {
    /**
     * Triggers a simple vibration pulse.
     * Handles SDK version checks for modern [VibrationEffect].
     */
    fun vibrate(context: Context, durationMillis: Long = 50L) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    durationMillis,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMillis)
        }
    }
}
