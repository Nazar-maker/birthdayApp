package com.example.birthdayapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.birthdayapp.HeartbeatConfig
import com.example.birthdayapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HeartbeatMessagingService : FirebaseMessagingService() {

    // Called when FCM assigns a new token (first install or token refresh).
    // We save it so the Cloud Function can look it up when pinging this device.
    override fun onNewToken(token: String) {
        FirebaseFirestore.getInstance()
            .document("users/${HeartbeatConfig.THIS_USER_ID}")
            .set(mapOf("fcmToken" to token), SetOptions.merge())
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data["type"] == "heartbeat") {
            playHeartbeatVibration()
            val senderName = message.data["senderName"] ?: "Someone"
            showNotification(senderName)
        }
    }

    private fun playHeartbeatVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }

        // lub-dub × 3  (lub = 150ms, tiny gap = 80ms, dub = 200ms, long gap = 700ms)
        val pattern = longArrayOf(
            0, 150, 80, 200, 700,
            150, 80, 200, 700,
            150, 80, 200, 700
        )
        val amplitudes = intArrayOf(
            0, 180, 0, 220, 0,
            180, 0, 220, 0,
            180, 0, 220, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun showNotification(senderName: String) {
        val channelId = "heartbeat_channel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Heartbeat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Heartbeat pings from your loved one"
                enableVibration(false) // vibration is handled manually above
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💗 Thinking of You")
            .setContentText("$senderName is thinking about you!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
