package ai.pill.alarm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ai.pill.alarm.screens.HomeScreen
import ai.pill.alarm.screens.LoginScreen
import ai.pill.alarm.screens.SplashScreen
import ai.pill.alarm.ui.theme.AIPillAlarmTheme

class MainActivity : AppCompatActivity() { // Must be AppCompatActivity for Biometric

    private val biometricManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AIPillAlarmTheme {
                val navController = rememberNavController()
                val biometricResult by biometricManager.promptResults.collectAsState(initial = null)

                // Handle Biometric Results
                LaunchedEffect(biometricResult) {
                    if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationError) {
                        Toast.makeText(this@MainActivity, "Auth Error", Toast.LENGTH_SHORT).show()
                    }
                }

                NavHost(navController = navController, startDestination = "splash") {

                    // 1. Splash Screen
                    composable("splash") {
                        SplashScreen(
                            onComplete = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Login Screen (Fingerprint)
                    composable("login") {
                        LoginScreen(
                            onLoginClick = {
                                biometricManager.showBiometricPrompt(
                                    title = "AI Pill Alarm",
                                    description = "Use your fingerprint to access your meds."
                                )
                            }
                        )
                    }

                    // 3. Home Screen
                    composable("home") {
                        HomeScreen(
                            onOpenAICamera = { /* Open Camera later */ },
                            onViewMedicines = { /* Navigate to All Medicines List later */ },
                            onViewSchedule = { /* Already here, do nothing */ }
                        )
                    }


                }
            }
        }
    }
}