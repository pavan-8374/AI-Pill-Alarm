package ai.pill.alarm.screens

import ai.pill.alarm.data.data.local.MedicineEntity
import ai.pill.alarm.userinterface.AlarmSchedulerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.pill.alarm.userinterface.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenAICamera: () -> Unit
) {
    val medicineList by viewModel.allMedicines.collectAsState(initial = emptyList())

    // --- STATE VARIABLES ---
    var showAlarmDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<MedicineEntity?>(null) }
    var medicineToDelete by remember { mutableStateOf<MedicineEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        // --- NEW: Solid Blue Header ---
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Medications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Gives it your Sky Blue background
                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Makes the text pure White
                )
            )
        },

        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 0.dp,
                contentPadding = PaddingValues(horizontal = 24.dp),
                modifier = Modifier.height(88.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(
                    onClick = onOpenAICamera,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(76.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Medicine", modifier = Modifier.size(36.dp))
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            // Removed the old "My Medications" text from here, as it's now in the TopAppBar!
            Spacer(modifier = Modifier.height(16.dp))

            if (medicineList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No medicines added yet.\nTap the + button to start!",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(medicineList) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            viewModel = viewModel,
                            onDelete = { medicineToDelete = medicine },
                            onSetAlarm = {
                                selectedMedicine = medicine
                                showAlarmDialog = true
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Delete Confirmation Dialog
    if (medicineToDelete != null) {
        AlertDialog(
            onDismissRequest = { medicineToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Delete Medication?", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${medicineToDelete?.name} from your list?", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMedicine(medicineToDelete!!)
                        medicineToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicineToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // 2. Set Alarm Dialog
    if (showAlarmDialog && selectedMedicine != null) {
        AlarmSchedulerDialog(
            initialSchedules = selectedMedicine!!.schedules,
            onDismiss = {
                showAlarmDialog = false
                selectedMedicine = null
            },
            onSave = { newSchedules ->
                val updatedMedicine = selectedMedicine!!.copy(schedules = newSchedules)
                viewModel.updateMedicine(updatedMedicine)
                showAlarmDialog = false
                selectedMedicine = null
            }
        )
    }
}