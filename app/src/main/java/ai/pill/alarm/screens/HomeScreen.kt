package ai.pill.alarm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.pill.alarm.data.data.local.MedicineEntity
import ai.pill.alarm.userinterface.HomeViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel, // We pass the ViewModel in so we can read the database!
    onOpenAICamera: () -> Unit,
    onViewMedicines: () -> Unit,
    onViewSchedule: () -> Unit
) {
    /**
     * STATE OBSERVATION:
     * 'collectAsState' connects to the active pipe (Flow) in your ViewModel.
     * If the database is empty, it starts with an 'emptyList()'.
     * Whenever a new pill is saved, 'medicineList' automatically updates,
     * and Jetpack Compose instantly redraws the screen!
     */
    val medicineList by viewModel.allMedicines.collectAsState(initial = emptyList())

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,

            // --- THUMB-ZONE NAVIGATION ---
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onOpenAICamera,
                    shape = CircleShape,
                    containerColor = Color(0xFF03A9F4),
                    contentColor = Color(0xFF0F2027),
                    modifier = Modifier.size(50.dp).offset(y = 70.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "AI Camera Scanner", modifier = Modifier.size(36.dp))
                }
            },
            floatingActionButtonPosition = FabPosition.Center,

            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF0F2027).copy(alpha = 0.95f),
                    contentColor = Color.White,
                    tonalElevation = 0.dp,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier.height(72.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
                        IconButton(onClick = onViewMedicines) {
                            Icon(Icons.Rounded.Medication, contentDescription = "All Medicines", modifier = Modifier.size(40.dp), tint = Color(0xFFB0BEC5))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
                        IconButton(onClick = onViewSchedule) {
                            Icon(Icons.Rounded.Schedule, contentDescription = "Today's Schedule", modifier = Modifier.size(40.dp), tint = Color(0xFF00C9FF))
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

                Text("Today's Schedule", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                // If there are no medicines, show a friendly prompt
                if (medicineList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No medicines added yet.\nTap the + button to start!", color = Color(0xFFB0BEC5), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                } else {
                    /**
                     * LAZY COLUMN:
                     * This is Android's highly efficient scrolling list.
                     * It only renders the items currently visible on the screen to save memory.
                     */
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        // Loop through the database list and create a Card for each one
                        items(medicineList) { medicine ->
                            MedicineCard(medicine = medicine)
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
                        }
                    }
                }
            }
        }
    }
}
/**
 * REUSABLE UI COMPONENT: The Pill Card
 * This card displays the individual medication data from the database.
 */
@Composable
fun MedicineCard(medicine: MedicineEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f)) // Glassmorphism effect
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // --- 1. THE DYNAMIC IMAGE UI ---
            // Check if the database has a saved image path for this pill
            if (!medicine.imageUriAsString.isNullOrEmpty()) {
                /**
                 * AsyncImage is from the Coil library.
                 * It safely loads the high-res image from the phone's storage
                 * without freezing the UI thread.
                 */
                AsyncImage(
                    model = medicine.imageUriAsString,
                    contentDescription = "Photo of ${medicine.name}",
                    contentScale = ContentScale.Crop, // Crops the image to fill the box perfectly
                    modifier = Modifier
                        .size(56.dp) // Make it big enough to see clearly
                        .clip(RoundedCornerShape(12.dp)) // Give the image rounded corners
                        .border(
                            1.dp,
                            Color(0xFF00C9FF).copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                )
            } else {
                // FALLBACK: If they didn't take a photo, show a nice generic icon box
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Medication,
                        contentDescription = "Generic Pill Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- 2. THE TEXT DATA ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = medicine.instructions,
                    color = Color(0xFFB0BEC5),
                    fontSize = 12.sp
                )
            }

            // --- 3. THE ALARM DATA ---
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = "Alarm Set",
                    tint = Color(0xFF00C9FF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Format the complex schedule list into a clean string (e.g., "08:00 AM, 12:00 PM")
                val timeString = if (medicine.schedules.isNotEmpty()) {
                    // Joins multiple times together separated by a comma
                    medicine.schedules.joinToString(", ") { it.time }
                } else {
                    "No Alarm"
                }

                Text(
                    text = timeString,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

