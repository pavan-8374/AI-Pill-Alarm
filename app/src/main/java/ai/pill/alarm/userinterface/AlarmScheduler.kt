package ai.pill.alarm.userinterface


import ai.pill.alarm.data.data.local.MedicineEntity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarmsForMedicine(medicine: MedicineEntity) {
        // 1. First, cancel any existing alarms for this pill so we don't get duplicates
        cancelAlarmsForMedicine(medicine)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // 2. Loop through every schedule the user created
        medicine.schedules.forEach { schedule ->
            try {
                val parsedDate = timeFormat.parse(schedule.time)
                val timeCalendar = Calendar.getInstance().apply {
                    if (parsedDate != null) time = parsedDate
                }

                val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
                val minute = timeCalendar.get(Calendar.MINUTE)

                // 3. Loop through every day selected (0=Sun, 1=Mon, etc.)
                schedule.days.forEach { dayOfWeekIndex ->
                    // Calendar uses 1=Sun, 2=Mon... so we add 1 to your 0-indexed days
                    val targetDayOfWeek = dayOfWeekIndex + 1

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.DAY_OF_WEEK, targetDayOfWeek)

                        // If that time has already passed this week, schedule it for NEXT week
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    }

                    // 4. Create the Intent that will trigger our AlarmReceiver
                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra("MEDICINE_NAME", medicine.name)
                        putExtra("MEDICINE_INSTRUCTIONS", medicine.instructions)
                        putExtra("MEDICINE_IMAGE_URI", medicine.imageUriAsString)
                    }

                    // Create a UNIQUE ID for this exact alarm (Medicine ID + Day + Hour + Minute)
                    val uniqueRequestCode = "${medicine.id}${targetDayOfWeek}${hour}${minute}".hashCode()

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        uniqueRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    // 5. Set the Exact Alarm!
                    try {
                        // Check exact alarm permission for Android 12+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            return@forEach // Skip if permission missing
                        }

                        // Sets an alarm that will wake the device from sleep
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAlarmsForMedicine(medicine: MedicineEntity) {
        // We recreate the exact same PendingIntents using the same IDs, and tell AlarmManager to cancel them
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        medicine.schedules.forEach { schedule ->
            try {
                val parsedDate = timeFormat.parse(schedule.time)
                val timeCalendar = Calendar.getInstance().apply { if (parsedDate != null) time = parsedDate }
                val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
                val minute = timeCalendar.get(Calendar.MINUTE)

                schedule.days.forEach { dayOfWeekIndex ->
                    val targetDayOfWeek = dayOfWeekIndex + 1
                    val uniqueRequestCode = "${medicine.id}${targetDayOfWeek}${hour}${minute}".hashCode()

                    val intent = Intent(context, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        uniqueRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}