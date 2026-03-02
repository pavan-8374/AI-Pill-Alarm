package ai.pill.alarm.userinterface

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 1. Get the medicine details we passed into the Intent
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Time for your medicine!"
        val medicineInstructions = intent.getStringExtra("MEDICINE_INSTRUCTIONS") ?: "Please take your scheduled dose."
        val imageUri = intent.getStringExtra("MEDICINE_IMAGE_URI")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 2. Create a Notification Channel (Required for Android 8.0+)
        val channelId = "pill_alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pill Alarms",
                NotificationManager.IMPORTANCE_HIGH // HIGH importance makes it pop up on screen!
            ).apply {
                description = "Reminders to take your medication"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("MEDICINE_NAME", medicineName)
            putExtra("MEDICINE_INSTRUCTIONS", medicineInstructions)
            putExtra("MEDICINE_IMAGE_URI", imageUri)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            medicineName.hashCode(), // Unique ID
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // --- Build the Notification with the Full-Screen Intent ---
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(" Time to take $medicineName")
            .setContentText(medicineInstructions)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Tells Android this is an urgent alarm
            .setFullScreenIntent(fullScreenPendingIntent, true) // For full screen notifications
            .setAutoCancel(true)
            .build()

        notificationManager.notify(medicineName.hashCode(), notification)
    }
}