package ai.pill.alarm.userinterface

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.*

data class AlarmSchedule(val time: String, val days: List<Int>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSchedulerDialog(
    initialSchedules: List<AlarmSchedule>,
    onDismiss: () -> Unit,
    onSave: (List<AlarmSchedule>) -> Unit
) {
    val currentSchedules = remember {
        mutableStateListOf<AlarmSchedule>().apply { addAll(initialSchedules) }
    }

    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = false)
    val selectedDays = remember { mutableStateListOf<Int>() }

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val fullDaysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // 1. Allow the dialog to use more screen width so text has room to breathe
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Keeps it from touching the absolute edges of the screen
        ) {
            Column(
                // 2. MAKE THE WHOLE DIALOG SCROLLABLE
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Schedule Medicine",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Set Time",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 3. SHRINK THE TIME PICKER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(0.85f), // Shrinks the dial to 85% of its normal size
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            selectorColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                            periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Select Days",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    daysOfWeek.forEachIndexed { index, day ->
                        val isSelected = selectedDays.contains(index)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                .clickable { if (isSelected) selectedDays.remove(index) else selectedDays.add(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(Calendar.MINUTE, timePickerState.minute)
                        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)

                        if (selectedDays.isNotEmpty()) {
                            currentSchedules.add(AlarmSchedule(formattedTime, selectedDays.toList()))
                            selectedDays.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDays.isNotEmpty()
                ) {
                    Text(
                        "+ Add Alarm",
                        color = if (selectedDays.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. CHANGED FROM LazyColumn TO REGULAR Column FOR COMPATIBILITY WITH VERTICAL SCROLL
                Column(modifier = Modifier.fillMaxWidth()) {
                    currentSchedules.forEach { schedule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val daysString = schedule.days.joinToString(", ") { fullDaysOfWeek[it] }

                            // 5. ADDED Modifier.weight(1f) TO PREVENT PUSHING THE DELETE BUTTON
                            Text(
                                text = "${schedule.time} — $daysString",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp) // Gives some space before the trash can
                            )

                            IconButton(
                                onClick = { currentSchedules.remove(schedule) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSave(currentSchedules) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Done", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}