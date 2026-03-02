package ai.pill.alarm.userinterface

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestAppPermissions() {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 1. Launcher for the Android 13+ Notification Permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // If they deny it, the app just won't show popups, but we still try to set alarms.
        }
    )

    // 2. Check and request permissions when this Composable loads
    LaunchedEffect(Unit) {
        // --- Request Notification Permission (Android 13+) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // --- Request Exact Alarm Permission (Android 12+) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if the user has already granted exact alarm permission
            if (!alarmManager.canScheduleExactAlarms()) {
                // If not, we have to send them to the system settings page to turn it on
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    // This flag ensures we return to the app after they change the setting
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
    }
}