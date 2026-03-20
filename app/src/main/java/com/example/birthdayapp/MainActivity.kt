package com.example.birthdayapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.birthdayapp.model.BirthdayBox
import com.example.birthdayapp.screens.DashboardScreen
import com.example.birthdayapp.screens.OpenWhenPlayerScreen
import com.example.birthdayapp.screens.BirthdayRevealScreen
import com.example.birthdayapp.theme.BirthdayAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Eagerly save this device's FCM token to Firestore so the Cloud
        // Function can always find it (onNewToken only fires on token refresh).
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseFirestore.getInstance()
                .document("users/${HeartbeatConfig.THIS_USER_ID}")
                .set(mapOf("fcmToken" to token), SetOptions.merge())
        }

        val prefs = getSharedPreferences("birthday_prefs", MODE_PRIVATE)
        val alreadyOpened = prefs.getBoolean("gift_opened", false)

        enableEdgeToEdge()
        setContent {
            BirthdayAppTheme {
                // Request POST_NOTIFICATIONS permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notifPermission = rememberPermissionState(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                    LaunchedEffect(Unit) {
                        if (!notifPermission.status.isGranted) {
                            notifPermission.launchPermissionRequest()
                        }
                    }
                }

                val isBahar = HeartbeatConfig.THIS_USER_ID == "user_b"

                // Start unlocked if the gift was already opened on a previous launch
                var isUnlocked by remember { mutableStateOf(!isBahar || alreadyOpened) }
                var selectedBox by remember { mutableStateOf<BirthdayBox?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when {
                            !isUnlocked -> BirthdayRevealScreen(
                                onPlayVideo = {
                                    prefs.edit().putBoolean("gift_opened", true).apply()
                                    isUnlocked = true
                                }
                            )
                            selectedBox != null -> OpenWhenPlayerScreen(
                                box = selectedBox!!,
                                onBack = { selectedBox = null }
                            )
                            else -> DashboardScreen(
                                onBoxClick = { selectedBox = it },
                                onReliveMoment = { isUnlocked = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
