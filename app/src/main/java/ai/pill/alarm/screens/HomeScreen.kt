package ai.pill.alarm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenAICamera: () -> Unit,
    onViewMedicines: () -> Unit,
    onViewSchedule: () -> Unit
) {
    // Premium Background Gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    // Mock data for previewing the UI
    val mockMedicines = listOf(
        MedicineMock("Aspirin", "08:00 AM", "Take with food"),
        MedicineMock("Vitamin D", "12:00 PM", "After lunch"),
        MedicineMock("Lisinopril", "08:00 PM", "Before bed")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent, // Let the gradient show through

            // --- THE NEW THUMB-ZONE NAVIGATION ---

            // 1. The Centered '+' Camera Button
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onOpenAICamera,
                    shape = CircleShape,
                    containerColor = Color(0xFF00C9FF), // Bright Cyan
                    contentColor = Color(0xFF0F2027),
                    modifier = Modifier
                        .size(72.dp) // Massive and easy to tap for elderly users
                        .offset(y = 8.dp) // Push it slightly down into the bar
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "AI Camera Scanner",
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,

            // 2. The Bottom Navigation Bar
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF0F2027).copy(alpha = 0.95f), // Dark Glass
                    contentColor = Color.White,
                    tonalElevation = 0.dp,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier.height(72.dp)
                ) {
                    // LEFT: All Uploaded Medicines
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onViewMedicines) {
                            Icon(
                                imageVector = Icons.Rounded.Medication,
                                contentDescription = "All Medicines",
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFFB0BEC5) // Unselected Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f)) // Creates the gap for the center FAB

                    // RIGHT: Today's Schedule (Clock)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onViewSchedule) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = "Today's Schedule",
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFF00C9FF) // Selected Cyan
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->

            // --- MAIN CONTENT AREA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Today's Schedule",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Medication List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockMedicines) { medicine ->
                        MedicineCard(medicine)
                    }
                    item {
                        // Extra space at bottom so the list doesn't hide behind the bottom bar
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// --- Reusable UI Component for individual pills ---
@Composable
fun MedicineCard(medicine: MedicineMock) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f)) // Glass effect
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Medication,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = medicine.instruction,
                    color = Color(0xFFB0BEC5),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = "Alarm Set",
                    tint = Color(0xFF00C9FF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = medicine.time,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class MedicineMock(val name: String, val time: String, val instruction: String)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onOpenAICamera = {},
        onViewMedicines = {},
        onViewSchedule = {}
    )
}

