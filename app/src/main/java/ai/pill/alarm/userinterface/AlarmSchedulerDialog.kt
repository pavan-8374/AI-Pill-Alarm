package ai.pill.alarm.userinterface

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

// The Data Model to hold the time and the selected days
data class AlarmSchedule(val time: String, val days: List<Int>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSchedulerDialog(
    onDismiss: () -> Unit,
    onSave: (List<AlarmSchedule>) -> Unit
) {
    // State to hold the temporary alarms being built in this dialog
    val currentSchedules = remember { mutableStateListOf<AlarmSchedule>() }

    // Time & Day States
    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = false)
    val selectedDays = remember { mutableStateListOf<Int>() }

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val fullDaysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF203A43)), // Dark Glass Color
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Schedule Medicine", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                // 1. Interactive Clock Dial (No Typing!)
                Text("Set Time", color = Color(0xFFB0BEC5), fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                // TimePicker
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color.White.copy(alpha = 0.05f),
                        clockDialSelectedContentColor = Color(0xFF0F2027),
                        clockDialUnselectedContentColor = Color.White,
                        selectorColor = Color(0xFF00C9FF),
                        containerColor = Color(0xFF203A43),
                        timeSelectorSelectedContainerColor = Color(0xFF00C9FF).copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = Color(0xFF00C9FF),
                        timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.1f),
                        timeSelectorUnselectedContentColor = Color.White,
                        periodSelectorSelectedContainerColor = Color(0xFF00C9FF),
                        periodSelectorSelectedContentColor = Color(0xFF0F2027),
                        periodSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.1f),
                        periodSelectorUnselectedContentColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Day Selector Row
                Text("Select Days", color = Color(0xFFB0BEC5), fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    daysOfWeek.forEachIndexed { index, day ->
                        val isSelected = selectedDays.contains(index)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF00C9FF) else Color.White.copy(alpha = 0.1f))
                                .clickable { if (isSelected) selectedDays.remove(index) else selectedDays.add(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = day, color = if (isSelected) Color(0xFF0F2027) else Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Add Alarm Button
                Button(
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(Calendar.MINUTE, timePickerState.minute)
                        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)

                        if (selectedDays.isNotEmpty()) {
                            currentSchedules.add(AlarmSchedule(formattedTime, selectedDays.toList()))
                            selectedDays.clear() // Reset days for the next alarm
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDays.isNotEmpty() // Only enable if they picked at least one day
                ) {
                    Text("+ Add Alarm", color = if (selectedDays.isNotEmpty()) Color(0xFF00C9FF) else Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. List of Added Alarms (Matches your screenshot!)
                LazyColumn(modifier = Modifier.heightIn(max = 120.dp)) {
                    items(currentSchedules) { schedule ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val daysString = schedule.days.joinToString(", ") { fullDaysOfWeek[it] }
                            Text("${schedule.time} — $daysString", color = Color.White, fontSize = 14.sp)

                            IconButton(onClick = { currentSchedules.remove(schedule) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Final Save Button
                Button(
                    onClick = { onSave(currentSchedules) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)), // Green for Done
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Done", color = Color(0xFF0F2027), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}