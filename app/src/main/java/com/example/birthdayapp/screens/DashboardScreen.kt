package com.example.birthdayapp.screens

import com.example.birthdayapp.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.birthdayapp.HeartbeatConfig
import com.example.birthdayapp.model.BirthdayBox
import com.example.birthdayapp.theme.DeepWarmBrown
import com.example.birthdayapp.theme.PureWhite
import com.example.birthdayapp.theme.SoftCoral
import com.example.birthdayapp.theme.SoftPinkShadow
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DashboardScreen(
    onBoxClick: (BirthdayBox) -> Unit,
    onReliveMoment: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }
    var heartbeatSent by remember { mutableStateOf(false) }

    val boxes = listOf(
        BirthdayBox(
            id = "1",
            title = "Miss Me",
            description = "A little message from me to you",
            emoji = "🥺",
            accentColor = 0xFFFFB5C8,
            audioResIds = listOf(R.raw.miss_me_1, R.raw.miss_me_2, R.raw.miss_me_3) // add R.raw.miss_me_1, R.raw.miss_me_2, etc.
        ),
        BirthdayBox(
            id = "2",
            title = "Hungry",
            description = "Something to make you smile",
            emoji = "🍓",
            accentColor = 0xFFFFD4A8,
            audioResIds = listOf(R.raw.hungry_1, R.raw.hungry_2, R.raw.hungry_3)
        ),
        BirthdayBox(
            id = "3",
            title = "Sad",
            description = "I've got something to cheer you up",
            emoji = "🫂",
            accentColor = 0xFFD4B8F0,
            audioResIds = listOf(R.raw.sad_1, R.raw.sad_2, R.raw.sad_3)
        ),
        BirthdayBox(
            id = "4",
            title = "Happy",
            description = "Let's celebrate together",
            emoji = "🎉",
            accentColor = 0xFFFFE8A8,
            audioResIds = listOf(R.raw.happy_1, R.raw.happy_2, R.raw.happy_3)
        )
    )

    val isBahar = HeartbeatConfig.THIS_USER_ID == "user_b"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isBahar) Arrangement.Top else Arrangement.Center
    ) {
        if (isBahar) {
            Spacer(modifier = Modifier.height(24.dp))
    
            Text(
                text = "Open When...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepWarmBrown
            )
            Text(
                text = "tap a card to hear a message",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepWarmBrown.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
    
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(boxes) { box ->
                    EnvelopeCard(box = box, onClick = { onBoxClick(box) })
                }
            }
    
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Primary action — Thinking of You
        Button(
            onClick = {
                if (!heartbeatSent) {
                    scope.launch {
                        db.collection("pings").add(
                            hashMapOf(
                                "from" to HeartbeatConfig.THIS_USER_ID,
                                "to" to HeartbeatConfig.OTHER_USER_ID,
                                "senderName" to HeartbeatConfig.SENDER_NAME,
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                        ).await()
                        heartbeatSent = true
                        delay(3000)
                        heartbeatSent = false
                    }
                }
            },
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (heartbeatSent) Color(0xFFE91E8C) else SoftCoral,
                contentColor = PureWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = SoftPinkShadow,
                    ambientColor = SoftPinkShadow
                )
        ) {
            Text(
                text = if (heartbeatSent) "💗 Sent!" else "💗 Thinking of You",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isBahar) {
            // Secondary action
            OutlinedButton(
                onClick = onReliveMoment,
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.5.dp, SoftCoral.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftCoral),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Relive Your Moment \uD83C\uDF81",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EnvelopeCard(box: BirthdayBox, onClick: () -> Unit) {
    val accentColor = Color(box.accentColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji in a soft accent circle
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(accentColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = box.emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Open when you're",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepWarmBrown.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = box.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepWarmBrown
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = box.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = DeepWarmBrown.copy(alpha = 0.55f)
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
