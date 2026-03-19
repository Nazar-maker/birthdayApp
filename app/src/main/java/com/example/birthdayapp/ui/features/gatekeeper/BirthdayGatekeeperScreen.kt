package com.example.birthdayapp.ui.features.gatekeeper

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

// Beautiful light, soft, cute colors
val SoftCreamBackground = Color(0xFFFFF7F8) // Warm snowy pinkish-white cream
val CuteTextDark = Color(0xFF4A3437) // Soft dark brownish-rose for high contrast elegance
val CuteTextLight = Color(0xFF907075) // Muted warm grey/rose for secondary text
val PinkAccent = Color(0xFFF48FB1)
val WarmRose = Color(0xFFD81B60)

enum class GatekeeperState {
    COUNTDOWN, REVEAL_TEASE, BIRTHDAY_MESSAGE, VIDEO_PLAYBACK
}

@Composable
fun BirthdayGatekeeperScreen(
    targetDate: LocalDate,
    videoResId: Int, // e.g., R.raw.birthday_video
    onUnlock: () -> Unit = {}
) {
    val now = LocalDate.now()
    var currentState by remember {
        mutableStateOf(
            if (now.isBefore(targetDate)) GatekeeperState.COUNTDOWN
            else GatekeeperState.REVEAL_TEASE
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCreamBackground),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = currentState,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            label = "GatekeeperCrossfade"
        ) { state ->
            when (state) {
                GatekeeperState.COUNTDOWN -> {
                    CountdownTimerView(targetDate = targetDate)
                }

                GatekeeperState.REVEAL_TEASE -> {
                    BoxRevealView(
                        onBoxOpened = {
                            currentState = GatekeeperState.BIRTHDAY_MESSAGE
                        }
                    )
                }
                
                GatekeeperState.BIRTHDAY_MESSAGE -> {
                    BirthdayMessageView(
                        onSeeVideo = {
                            currentState = GatekeeperState.VIDEO_PLAYBACK
                        }
                    )
                }

                GatekeeperState.VIDEO_PLAYBACK -> {
                    VideoPlaybackView(
                        videoResId = videoResId,
                        onUnlock = onUnlock
                    )
                }
            }
        }
    }
}

@Composable
fun CountdownTimerView(targetDate: LocalDate) {
    var timeRemaining by remember { mutableLongStateOf(0L) }
    
    val targetDateTime = targetDate.atStartOfDay()

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            val duration = Duration.between(now, targetDateTime)
            if (duration.isNegative || duration.isZero) {
                timeRemaining = 0L
                break
            }
            timeRemaining = duration.toMillis()
            delay(1000)
        }
    }

    val days = (timeRemaining / (1000 * 60 * 60 * 24))
    val hours = ((timeRemaining / (1000 * 60 * 60)) % 24)
    val minutes = ((timeRemaining / 1000 / 60) % 60)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Heartbeat",
            tint = PinkAccent,
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
        )
        
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeSection(value = days, label = "Days")
            TimeSection(value = hours, label = "Hours")
            TimeSection(value = minutes, label = "Mins")
        }

        Spacer(modifier = Modifier.height(72.dp))

        Text(
            text = "Patience... good things take time",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Serif,
            color = CuteTextLight,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TimeSection(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            fontSize = 54.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            color = CuteTextDark
        )
        Text(
            text = label.uppercase(),
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            letterSpacing = 2.sp,
            color = CuteTextLight
        )
    }
}

@Composable
fun BoxRevealView(onBoxOpened: () -> Unit) {
    var isTapped by remember { mutableStateOf(false) }

    // Floating/Pulsing tease
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "box_float"
    )

    // Spring pop on tap
    val scaleAnim by animateFloatAsState(
        targetValue = if (isTapped) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "box_pop"
    )

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(1200) // Wait for the pop animation to almost finish
            onBoxOpened()
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isTapped) {
            ParticleExplosion()
        }

        // Tap instructions (fades out when tapped)
        if (!isTapped) {
            Text(
                text = "Tap to open",
                color = CuteTextLight,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
            )
        }

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { isTapped = true }
                )
                .scale(scaleAnim)
                // Appears to float continuously, locks height when tapped/opened
                .offset(y = if (!isTapped) floatAnim.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            // Stylized box/envelope placeholder
            Icon(
                imageVector = Icons.Filled.Favorite, // Swap out for Vector Gift/Envelope Drawables when ready!
                contentDescription = "Gift Box",
                tint = WarmRose,
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@Composable
fun ParticleExplosion() {
    val particles = remember { List(40) { Particle() } }
    var tick by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            tick += 1f
            particles.forEach { it.update() }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            if (particle.alpha > 0) {
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = Offset(size.width / 2 + particle.x, size.height / 2 + particle.y)
                )
            }
        }
    }
}

class Particle {
    var x = 0f
    var y = 0f
    var vx = Random.nextFloat() * 30 - 15
    var vy = Random.nextFloat() * -30 - 5
    var size = Random.nextFloat() * 12 + 8
    var alpha = 1f
    val color = listOf(Color(0xFFF48FB1), Color(0xFFFFCDD2), Color(0xFFD81B60), Color.White).random()

    fun update() {
        x += vx
        y += vy
        vy += 0.8f // gravity
        alpha -= 0.015f
    }
}

@Composable
fun BirthdayMessageView(onSeeVideo: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Beautiful soft romantic gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFE4E1), Color(0xFFF48FB1)) // Soft rosy gradient
                    )
                )
        )
        
        // Soft overlay tint to make the white text universally readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.05f))
        )

        // Overlay Text and Button layer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp) // extra padding to keep text safe inside the box
        ) {
            Text(
                text = "Happy Birthday!\n\uD83D\uDC95", // Wrapped nicely with beautiful double hearts!
                fontSize = 38.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center, // Fixed orientation!
                lineHeight = 44.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "I made this app just for you. Every box inside is waiting.",
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center, // Keep it beautifully centered
                modifier = Modifier.padding(bottom = 64.dp)
            )

            // Minimalist continue button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onSeeVideo() }
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "See Video",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmRose, // Button text matches the romantic theme instead of plain white
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun VideoPlaybackView(videoResId: Int, onUnlock: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Video Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
        ) {
            if (videoResId != 0) {
                // Play actual video using standard Android VideoView
                // which handles some MP4 encodings better than ExoPlayer's strict demuxer
                AndroidView(
                    factory = { ctx ->
                        android.widget.VideoView(ctx).apply {
                            val uri = Uri.parse("android.resource://${ctx.packageName}/$videoResId")
                            setVideoURI(uri)
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating skip/continue button directly over the video
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.4f)) // Subtle glass effect
                .clickable { onUnlock() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Open Your Gifts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}
