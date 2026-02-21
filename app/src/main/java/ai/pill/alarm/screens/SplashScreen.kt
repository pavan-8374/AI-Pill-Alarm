package ai.pill.alarm.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    // Animation States
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth, premium fade-in
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "AlphaAnim"
    )

    // Elegant scale-up for the logo
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ScaleAnim"
    )

    // Subtle slide-up for text
    val slideAnim by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "SlideAnim"
    )

    // Timer
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Hold the screen for 2.5 seconds
        onComplete()
    }

    // Premium Dark Gradient Background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027), // Deep Dark Blue/Black
            Color(0xFF203A43), // Dark Teal
            Color(0xFF2C5364)  // Slate/Teal
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            // --- Premium Logo Container (Glassmorphism effect) ---
            Box(
                modifier = Modifier
                    .scale(scaleAnim)
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.1f)) // Translucent base
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f), // Glass edge reflection
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner pure white circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Medication,
                        contentDescription = "Pill Icon",
                        tint = Color(0xFF2C5364), // Matches the gradient
                        modifier = Modifier.size(40.dp)
                    )

                    // The "AI" Sparkle Touch floating nearby
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "AI Sparkle",
                        tint = Color(0xFF00C9FF), // Bright tech blue/cyan
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 18.dp, y = (-18).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Typography ---
            Text(
                text = "AI Pill Alarm",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier.offset(y = slideAnim)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SMART HEALTH COMPANION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00C9FF), // Accent color matching the sparkle
                letterSpacing = 4.sp,
                modifier = Modifier.offset(y = slideAnim)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onComplete = {})
}