package com.example.birthdayapp.ui.features.openwhen

import android.media.MediaPlayer
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.birthdayapp.data.model.BirthdayBox
import com.example.birthdayapp.ui.theme.DeepWarmBrown
import com.example.birthdayapp.ui.theme.MistyRose
import com.example.birthdayapp.ui.theme.PureWhite
import com.example.birthdayapp.ui.theme.SoftCoral
import com.example.birthdayapp.ui.theme.SoftPearl

@Composable
fun OpenWhenPlayerScreen(
    box: BirthdayBox,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val accentColor = Color(box.accentColor)

    // Pick a random recording each time this screen opens
    val audioResId = remember { box.audioResIds.randomOrNull() }
    val mediaPlayer = remember(audioResId) {
        audioResId?.let { MediaPlayer.create(context, it) }
    }
    var isPlaying by remember { mutableStateOf(false) }

    // Auto-play when the screen opens
    LaunchedEffect(mediaPlayer) {
        mediaPlayer?.let {
            it.start()
            isPlaying = true
            it.setOnCompletionListener { isPlaying = false }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SoftPearl, MistyRose)))
    ) {
        // Back button
        IconButton(
            onClick = {
                mediaPlayer?.stop()
                onBack()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DeepWarmBrown
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "open when you're",
                style = MaterialTheme.typography.bodyLarge,
                color = DeepWarmBrown.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = box.title.uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = DeepWarmBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Emoji with pulsing rings — pulses while playing
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(if (isPlaying) pulseScale else 1f)
                        .background(accentColor.copy(alpha = 0.25f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(accentColor.copy(alpha = 0.45f), CircleShape)
                )
                Text(text = box.emoji, fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(56.dp))

            if (audioResId != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(SoftCoral, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        mediaPlayer?.let { player ->
                            if (isPlaying) {
                                player.pause()
                                isPlaying = false
                            } else {
                                player.start()
                                isPlaying = true
                                player.setOnCompletionListener { isPlaying = false }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = PureWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPlaying) "playing..." else "tap to play",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepWarmBrown.copy(alpha = 0.5f)
                )
            } else {
                Text(
                    text = "recording coming soon \uD83C\uDF99\uFE0F",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepWarmBrown.copy(alpha = 0.5f)
                )
            }
        }
    }
}
