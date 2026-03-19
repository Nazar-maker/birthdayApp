package com.example.birthdayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.birthdayapp.ui.features.dashboard.DashboardScreen
import com.example.birthdayapp.ui.features.reveal.BirthdayRevealScreen
import com.example.birthdayapp.ui.theme.BirthdayAppTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirthdayAppTheme {
                var isUnlocked by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (!isUnlocked) {
                            // Using the brand new cute & romantic Reveal Screen!
                            BirthdayRevealScreen(
                                onPlayVideo = { isUnlocked = true }
                            )
                        } else {
                            DashboardScreen(
                                onBoxClick = {},
                                onReliveMoment = { isUnlocked = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
