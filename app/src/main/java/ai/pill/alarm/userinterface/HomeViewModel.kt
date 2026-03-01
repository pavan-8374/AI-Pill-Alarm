package ai.pill.alarm.userinterface

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ai.pill.alarm.data.data.local.MedicineEntity
import ai.pill.alarm.data.data.repository.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * THE VIEW MODEL
 * This acts as the bridge between your UI (HomeScreen) and your Data (Repository/Database).
 */
class HomeViewModel(private val repository: MedicineRepository) : ViewModel() {

    /**
     * THIS IS THE MISSING VARIABLE!
     * We grab the Flow (the open pipe) from the database and convert it into a 'StateFlow'.
     * StateFlow is specifically designed for Jetpack Compose UIs to read safely.
     * Whenever a new pill is added to the database, this variable updates automatically!
     */
    val allMedicines: StateFlow<List<MedicineEntity>> = repository.getAllMedicines()
        .stateIn(
            scope = viewModelScope, // Ties this to the lifecycle of the ViewModel
            started = SharingStarted.WhileSubscribed(5000), // Keeps it active while UI is visible
            initialValue = emptyList() // Start with an empty list while loading
        )

    // Function to save a new medication
    fun addMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }
}

/**
 * THE FACTORY
 * Because our ViewModel requires a Repository parameter, we must use a Factory to build it.
 * We set this up earlier in your MainActivity!
 */
class HomeViewModelFactory(private val repository: MedicineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}