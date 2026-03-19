package com.example.birthdayapp.ui.features.gatekeeper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.birthdayapp.ui.components.TactileBox

@Composable
fun GatekeeperScreen(
    viewModel: GatekeeperViewModel = viewModel(),
    onBoxOpened: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = spring()).togetherWith(fadeOut(animationSpec = spring()))
            },
            label = "GatekeeperTransition"
        ) { state ->
            when (state) {
                is GatekeeperUiState.Loading -> {
                    Text(text = "Loading...", color = MaterialTheme.colorScheme.onBackground)
                }
                is GatekeeperUiState.Locked -> {
                    CountdownTimer(state)
                }
                is GatekeeperUiState.Unlocked -> {
                    UnlockedView(onBoxOpened = onBoxOpened)
                }
            }
        }
    }
}

@Composable
fun CountdownTimer(state: GatekeeperUiState.Locked) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "COMING SOON",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TimeUnit(value = state.days, label = "DAYS")
            TimeUnit(value = state.hours, label = "HOURS")
            TimeUnit(value = state.minutes, label = "MINS")
            TimeUnit(value = state.seconds, label = "SECS")
        }
    }
}

@Composable
fun TimeUnit(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun UnlockedView(onBoxOpened: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "IT'S TIME",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraLight,
            letterSpacing = 8.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        TactileBox(
            title = "Open Me",
            onClick = onBoxOpened
        )
    }
}