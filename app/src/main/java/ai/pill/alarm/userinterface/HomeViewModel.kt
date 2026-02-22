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

class HomeViewModel(private val repository: MedicineRepository) : ViewModel() {

    // Converts the standard Flow into a StateFlow, which Jetpack Compose loves!
    // It automatically updates the UI whenever the database changes.
    val medicines: StateFlow<List<MedicineEntity>> = repository.allMedicines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Function to add a medicine (Called when you click "Save" on an Add Screen)
    fun addMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }

    // Function to delete a medicine
    fun deleteMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
        }
    }
}

// A Factory is required to pass the Repository into the ViewModel
class HomeViewModelFactory(private val repository: MedicineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
