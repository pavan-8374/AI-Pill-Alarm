package ai.pill.alarm.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    // Premium background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    // Gentle pulse animation for the fingerprint button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                // Adjusted padding: Pushes text down from the very top, and button up from the very bottom
                .padding(top = 80.dp, bottom = 64.dp, start = 32.dp, end = 32.dp)
        ) {
            // --- Top Section: Welcome Text ---
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Unlock your secure health vault to manage medications and get AI insights.",
                fontSize = 14.sp,
                color = Color(0xFFB0BEC5), // Light Blue/Gray
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // --- The Magic Spacer ---
            // This takes up ALL the empty space in the middle,
            // naturally pushing the fingerprint button to the bottom!
            Spacer(modifier = Modifier.weight(1f))

            // --- Bottom Section: Glowing Fingerprint Button ---
            Box(
                modifier = Modifier
                    .scale(pulseScale) // Apply the pulse animation
                    .size(120.dp)
                    .shadow(24.dp, CircleShape, spotColor = Color(0xFF00C9FF)) // Outer glow
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)) // Translucent glass base
                    .border(
                        width = 1.dp,
                        color = Color(0xFF00C9FF).copy(alpha = 0.5f), // Cyan edge reflection
                        shape = CircleShape
                    )
                    .clickable { onLoginClick() }, // Triggers the Biometric Prompt
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fingerprint,
                    contentDescription = "Fingerprint Login",
                    tint = Color(0xFF00C9FF), // Bright Cyan
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Call to Action ---
            Text(
                text = "Tap to Authenticate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00C9FF),
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = {})
}