package ai.pill.alarm.screens

import ai.pill.alarm.data.data.local.MedicineEntity
import ai.pill.alarm.userinterface.AlarmSchedulerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.pill.alarm.userinterface.HomeViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMedicinesScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val medicineList by viewModel.allMedicines.collectAsState(initial = emptyList())

    // --- NEW: State variables to handle the Alarm Dialog ---
    var showAlarmDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<MedicineEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("All Medicines", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            if (medicineList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Your medicine cabinet is empty.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(medicineList) { medicine ->

                        // --- FIX: Pass the missing parameters to MedicineCard ---
                        MedicineCard(
                            medicine = medicine,
                            viewModel = viewModel,
                            onDelete = {
                                // Tells the ViewModel to delete this specific pill from the database
                                viewModel.deleteMedicine(medicine)
                            },
                            onSetAlarm = {
                                // Remembers which pill we tapped and opens the dialog
                                selectedMedicine = medicine
                                showAlarmDialog = true
                            }
                        )

                    }
                }
            }
        }
    }

    // Show the Alarm Dialog when triggered
    if (showAlarmDialog && selectedMedicine != null) {
        AlarmSchedulerDialog(
            initialSchedules = selectedMedicine!!.schedules,
            onDismiss = {
                showAlarmDialog = false
                selectedMedicine = null
            },
            onSave = { newSchedules ->

                val updatedMedicine = selectedMedicine!!.copy(schedules = newSchedules)

                //  Tell the ViewModel to update this pill in the database
                viewModel.updateMedicine(updatedMedicine)

                //  Close the dialog and clean up
                showAlarmDialog = false
                selectedMedicine = null
            }
        )
    }
}