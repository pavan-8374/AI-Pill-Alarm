package ai.pill.alarm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.pill.alarm.userinterface.HomeViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMedicinesScreen(
    viewModel: HomeViewModel, // We pass the Brain in to read the database
    onBack: () -> Unit        // A function to handle the physical back arrow
) {
    // 1. Grab the live list of medicines directly from the database
    val medicineList by viewModel.allMedicines.collectAsState(initial = emptyList())

    // 2. Use the same premium gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,

            // 3. Add a Top Bar with a Back Arrow
            topBar = {
                TopAppBar(
                    title = { Text("All Medicines", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->

            // 4. Show the list of pills!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                if (medicineList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your medicine cabinet is empty.", color = Color(0xFFB0BEC5))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(medicineList) { medicine ->
                            // We reuse the exact same MedicineCard you built for the HomeScreen!
                            MedicineCard(medicine = medicine)
                        }
                    }
                }
            }
        }
    }
}