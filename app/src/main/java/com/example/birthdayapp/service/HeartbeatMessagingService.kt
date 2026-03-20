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

        // More intense: lub-dub × 3  (lub = 150ms, tiny gap = 80ms, dub = 250ms, long gap = 600ms)
        val pattern = longArrayOf(
            0, 150, 80, 250, 600,
            150, 80, 250, 600,
            150, 80, 250, 600
        )
        // Amplitudes increased to max (255 represents maximum amplitude)
        val amplitudes = intArrayOf(
            0, 255, 0, 255, 0,
            255, 0, 255, 0,
            255, 0, 255, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun showNotification(senderName: String) {
        // Change the channel ID so Android rebuilds it with the new silent rules
        val channelId = "heartbeat_channel_silent"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Heartbeat",
                NotificationManager.IMPORTANCE_HIGH // HIGH is needed to show popup on screen
            ).apply {
                description = "Heartbeat pings from your loved one"
                enableVibration(false) // vibration is handled manually above
                setSound(null, null) // SILENT: Remove notification sound entirely
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💗 Thinking of You")
            .setContentText("$senderName is thinking about you!")
            .setPriority(NotificationCompat.PRIORITY_HIGH) 
            .setDefaults(0) // Remove any default notification effects (like sound)
            .setVibrate(longArrayOf(0L)) // Add a dummy vibration to prevent system default vibration override
            .setSound(null) // Ensure sound is completely silent
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
