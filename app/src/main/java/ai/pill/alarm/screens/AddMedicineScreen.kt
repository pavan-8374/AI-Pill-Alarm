package ai.pill.alarm.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import ai.pill.alarm.userinterface.AlarmSchedulerDialog
import ai.pill.alarm.userinterface.AlarmSchedule


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- 1. STATE VARIABLES ---
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    var pillName by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    var isFlipped by remember { mutableStateOf(false) }
    var showDeleteWarning by remember { mutableStateOf(false) }

    // Schedule States
    val savedSchedules = remember { mutableStateListOf<AlarmSchedule>() }
    var showAlarmDialog by remember { mutableStateOf(false) }

    // --- 2. LAUNCHERS & ANIMATIONS ---
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "FlipAnimation"
    )

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) capturedImageUri = tempImageUri
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    // --- 3. MAIN UI ---
    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Add New Pill", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // --- 3D FLIP IMAGE CARD ---
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { if (capturedImageUri != null) isFlipped = !isFlipped }
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFF00C9FF).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // FRONT OF CARD
                    if (capturedImageUri != null) {
                        AsyncImage(
                            model = capturedImageUri,
                            contentDescription = "Captured Pill",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        IconButton(
                            onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Rounded.CameraAlt, contentDescription = "Take Photo", tint = Color(0xFFB0BEC5), modifier = Modifier.size(48.dp))
                        }
                    }
                } else {
                    // BACK OF CARD (Image Management)
                    Column(
                        modifier = Modifier.graphicsLayer { rotationY = 180f },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9FF)),
                            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 16.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color(0xFF0F2027))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retake Photo", color = Color(0xFF0F2027), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showDeleteWarning = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Image", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (capturedImageUri != null) {
                Text("Tap image to flip", color = Color(0xFF00C9FF), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- TEXT FIELDS ---
            OutlinedTextField(
                value = pillName,
                onValueChange = { pillName = it },
                label = { Text("Pill Name (e.g., Aspirin)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00C9FF), unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color(0xFF00C9FF), unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (e.g., Take with food)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00C9FF), unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color(0xFF00C9FF), unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- SCHEDULE SECTION ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alarms", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showAlarmDialog = true }) {
                    Text(if (savedSchedules.isEmpty()) "+ Set Schedule" else "Edit Schedule", color = Color(0xFF00C9FF))
                }
            }

            // Display the saved schedules
            savedSchedules.forEach { schedule ->
                val fullDaysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                val daysString = schedule.days.joinToString(", ") { fullDaysOfWeek[it] }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(schedule.time, color = Color(0xFF00C9FF), fontWeight = FontWeight.Bold)
                    Text(daysString, color = Color(0xFFB0BEC5), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Entry Button
            Button(
                onClick = { /* TODO: Save to Room DB */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9FF)),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Medication", color = Color(0xFF0F2027), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- 4. DIALOGS ---

    // Delete Warning Dialog
    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            containerColor = Color(0xFF203A43),
            title = { Text("Delete Image?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this photo?", color = Color(0xFFB0BEC5)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        capturedImageUri = null
                        isFlipped = false
                        showDeleteWarning = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWarning = false }) {
                    Text("Cancel", color = Color(0xFF00C9FF))
                }
            }
        )
    }

    // Trigger the external Alarm Scheduler Dialog
    if (showAlarmDialog) {
        AlarmSchedulerDialog(
            onDismiss = { showAlarmDialog = false },
            onSave = { newSchedules ->
                savedSchedules.clear()
                savedSchedules.addAll(newSchedules)
                showAlarmDialog = false
            }
        )
    }
}

// Helper function
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "PILL_" + timeStamp + "_"
    val storageDir = File(cacheDir, "images").apply { mkdirs() }
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}