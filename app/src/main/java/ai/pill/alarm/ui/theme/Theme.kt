package ai.pill.alarm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- 1. Define Your Brand Colors ---
val SkyBlue = Color(0xFF03A9F4) // A beautiful, crisp Sky Blue
val PureWhite = Color(0xFFFFFFFF)
val DarkText = Color(0xFF1C1B1F)

// Dark Theme Colors (Matching your Splash/Login screens)
val DarkBg = Color(0xFF0F2027)
val DarkSurface = Color(0xFF203A43)
val CyanAccent = Color(0xFF00C9FF)

// --- 2. Light Theme (Calm, Medical, Professional) ---
private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    secondary = SkyBlue,
    tertiary = SkyBlue,
    background = PureWhite,
    surface = PureWhite,
    onPrimary = PureWhite, // White text/icons on Sky Blue buttons
    onBackground = DarkText, // Text color on background
    onSurface = DarkText
)

// --- 3. Dark Theme (Premium, AI-vibe) ---
private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    secondary = CyanAccent,
    tertiary = CyanAccent,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onBackground = PureWhite,
    onSurface = PureWhite
)

@Composable
fun AIPillAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor to FALSE so Android doesn't override your branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}