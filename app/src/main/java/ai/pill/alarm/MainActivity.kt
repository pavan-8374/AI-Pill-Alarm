package ai.pill.alarm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ai.pill.alarm.data.data.local.MedicineDatabase
import ai.pill.alarm.data.data.repository.MedicineRepository
import ai.pill.alarm.screens.HomeScreen
import ai.pill.alarm.screens.SplashScreen
import ai.pill.alarm.screens.LoginScreen
import ai.pill.alarm.userinterface.HomeViewModel
import ai.pill.alarm.userinterface.HomeViewModelFactory
import ai.pill.alarm.userinterface.RequestAppPermissions
import ai.pill.alarm.screens.AddMedicineScreen
import ai.pill.alarm.userinterface.AlarmScheduler

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MedicineDatabase.getInstance(applicationContext)
        val repository = MedicineRepository(database.dao)

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var isAuthenticated by remember { mutableStateOf(false) }

            // Listen for biometric results
            val promptResult by promptManager.promptResults.collectAsState(initial = null)

            // 1. Check for Biometric Capability
            LaunchedEffect(Unit) {
                if (!checkDeviceCapability()) {
                    // If device can't do biometrics, just let them in
                    isAuthenticated = true
                }
            }

            // 2. Handle successful authentication
            LaunchedEffect(promptResult) {
                if (promptResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                    isAuthenticated = true
                }
            }

            // --- UI LOGIC ---
            if (showSplash) {
                SplashScreen(onComplete = { showSplash = false })
            } else if (!isAuthenticated) {
                LoginScreen(onLoginClick = {
                    promptManager.showBiometricPrompt(
                        title = "Secure Login",
                        description = "Authenticate to access your medications"
                    )
                })
            } else {
                // Main App Content
                RequestAppPermissions()
                val alarmScheduler = AlarmScheduler(applicationContext)
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(repository,alarmScheduler)
                )
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onOpenAICamera = { navController.navigate("add_medicine") }
                        )
                    }
                    composable("add_medicine") {
                        AddMedicineScreen(
                            viewModel = homeViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    /**
     * Checks if the device has biometric hardware and if the user has enrolled.
     * Returns true if biometrics are available and ready.
     */
    private fun checkDeviceCapability(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric hardware detected", Toast.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware is busy or unavailable", Toast.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Device supports it, but user hasn't set up a fingerprint/PIN
                Toast.makeText(this, "No biometrics enrolled in device settings", Toast.LENGTH_SHORT).show()
                false
            }
            else -> false
        }
    }
}