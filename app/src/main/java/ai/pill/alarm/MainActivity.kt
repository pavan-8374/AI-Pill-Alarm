package ai.pill.alarm // Make sure this matches your actual package name!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ai.pill.alarm.data.data.local.MedicineDatabase
import ai.pill.alarm.data.data.repository.MedicineRepository
import ai.pill.alarm.screens.AddMedicineScreen
import ai.pill.alarm.screens.HomeScreen
import ai.pill.alarm.userinterface.HomeViewModel
import ai.pill.alarm.userinterface.HomeViewModelFactory
import ai.pill.alarm.screens.AllMedicinesScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the Database and Repository
        val database = MedicineDatabase.getInstance(applicationContext)
        val repository = MedicineRepository(database.dao)

        setContent {
            // 2. Create the ViewModel using the Factory we built earlier
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )

            // 3. Set up the Navigation
            val navController = rememberNavController()

            // splash screen or biometric login, keep them here!

            NavHost(navController = navController, startDestination = "home") {

                composable("home") {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onOpenAICamera = { navController.navigate("add_medicine") },
                        onViewMedicines = { navController.navigate("all_medicines") },
                        onViewSchedule = { /* TODO */ }
                    )
                }
                composable("add_medicine") {
                    AddMedicineScreen(
                        viewModel = homeViewModel, // Pass the ViewModel
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("all_medicines") {
                    AllMedicinesScreen(
                        viewModel = homeViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}