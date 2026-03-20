package com.example.birthdayapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.media.MediaPlayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import android.view.LayoutInflater
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.birthdayapp.R
import com.example.birthdayapp.theme.SoftPearl
import com.example.birthdayapp.theme.MistyRose
import com.example.birthdayapp.theme.SoftCoral
import com.example.birthdayapp.theme.DeepWarmBrown
import com.example.birthdayapp.theme.SoftPinkShadow
import com.example.birthdayapp.theme.PureWhite

@Composable
fun BirthdayRevealScreen(onPlayVideo: () -> Unit) {
    var isOpened by remember { mutableStateOf(false) }
    var isVideoVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = RawResourceDataSource.buildRawResourceUri(R.raw.birthday)
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false // DO NOT AUTOPLAY when screen first loads!
            // repeatMode is REPEAT_MODE_OFF by default, so it won't loop!
        }
    }

    LaunchedEffect(isVideoVisible) {
        if (isVideoVisible) {
            delay(300) // allow the video layout to expand
            scrollState.animateScrollTo(scrollState.maxValue)
            exoPlayer.playWhenReady = true // Start playback ONLY after scrolling is revealed!
        }
    }

    LaunchedEffect(isOpened) {
        if (isOpened) {
            MediaPlayer.create(context, R.raw.confetti_sound).apply {
                start()
                setOnCompletionListener { it.release() }
            }
        }
    }

    // Ensure ExoPlayer is released when the screen is navigated away from
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Load Lottie Compositions
    val giftComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gift_pink))
    val confettiComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))

    // Gift Animation State: Play the first few frames as an idle loop, then play to end when opened
    val giftProgress by animateLottieCompositionAsState(
        composition = giftComposition,
        iterations = if (isOpened) 1 else LottieConstants.IterateForever,
        isPlaying = true,
        clipSpec = if (isOpened) LottieClipSpec.Progress(0f, 1f) else LottieClipSpec.Progress(0f, 0.4f),
        restartOnPlay = false
    )

    // Confetti Animation State
    val confettiProgress by animateLottieCompositionAsState(
        composition = confettiComposition,
        iterations = 1,
        isPlaying = isOpened
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // NEW DESIGN SYSTEM: Soft, Warm Gradient Background
            .background(
                Brush.verticalGradient(
                    colors = listOf(SoftPearl, MistyRose)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Main Content Layer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState).fillMaxWidth()
        ) {
            // Gift Animation
            Box(
                modifier = Modifier
                    .size(400.dp) // Much larger gift box!
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isOpened
                    ) {
                        isOpened = true
                    },
                contentAlignment = Alignment.Center
            ) {
                // UNOPENED: Idle loop frames. OPENED: Plays to final frame naturally.
                LottieAnimation(
                    composition = giftComposition,
                    progress = { giftProgress },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Unopened State Text
            AnimatedVisibility(
                visible = !isOpened,
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = "Tap to open your gift",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = DeepWarmBrown.copy(alpha = 0.7f) // Softer, less harsh than black
                )
            }

            // Opened State Texts and Buttons
            AnimatedVisibility(
                visible = isOpened,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Happy Birthday Baharym! \uD83D\uDC95",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepWarmBrown,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (!isVideoVisible) {
                        Button(
                            onClick = { isVideoVisible = true },
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftCoral,
                                contentColor = PureWhite
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(32.dp),
                                    spotColor = SoftPinkShadow,
                                    ambientColor = SoftPinkShadow
                                )
                        ) {
                            Text(
                                text = "Play Video",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Embedded Native Video Player!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(9f / 16f)
                                .padding(vertical = 16.dp)
                                .shadow(16.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(DeepWarmBrown)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    // Inflate with surface_type="texture_view" so the player
                                    // scrolls correctly — SurfaceView renders on a separate layer
                                    // and glitches inside verticalScroll containers.
                                    (LayoutInflater.from(ctx)
                                        .inflate(R.layout.player_view, null) as PlayerView)
                                        .apply {
                                            player = exoPlayer
                                            useController = true
                                            setShowNextButton(false)
                                            setShowPreviousButton(false)
                                            setShowFastForwardButton(false)
                                            setShowRewindButton(false)
                                            controllerShowTimeoutMs = 2500
                                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Button to proceed to the main app dashboard
                        Button(
                            onClick = onPlayVideo,
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftCoral,
                                contentColor = PureWhite
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .graphicsLayer { 
                                    // Hardware acceleration clipping fix for shadows!
                                    renderEffect = androidx.compose.ui.graphics.BlurEffect(0f, 0f)
                                }
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(32.dp),
                                    spotColor = SoftPinkShadow,
                                    ambientColor = SoftPinkShadow
                                )
                        ) {
                            Text(
                                text = "Continue To Surprises \uD83D\uDC96",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // Confetti Overlay Layer
        if (isOpened && confettiProgress < 1f) {
            LottieAnimation(
                composition = confettiComposition,
                progress = { confettiProgress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
