package com.example.birthdayapp.ui.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.birthdayapp.data.model.BirthdayBox
import com.example.birthdayapp.ui.theme.PureWhite
import com.example.birthdayapp.ui.theme.SoftCoral
import com.example.birthdayapp.ui.theme.SoftPinkShadow

@Composable
fun DashboardScreen(
    onBoxClick: (BirthdayBox) -> Unit,
    onReliveMoment: () -> Unit
) {
    val boxes = listOf(
        BirthdayBox("1", "Miss Me", "Open when you miss me"),
        BirthdayBox("2", "Hungry", "Open when you are hungry"),
        BirthdayBox("3", "Sad", "Open when you are sad"),
        BirthdayBox("4", "Happy", "Open when you are happy")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "OPEN WHEN...",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f), // pushes the button below to the bottom
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(boxes) { box ->
                BoxCard(box = box, onClick = { onBoxClick(box) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Relive Your Moment Button
        Button(
            onClick = onReliveMoment,
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SoftCoral,
                contentColor = PureWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = SoftPinkShadow,
                    ambientColor = SoftPinkShadow
                )
        ) {
            Text(
                text = "Relive Your Moment \uD83C\uDF81",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BoxCard(box: BirthdayBox, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = box.title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}