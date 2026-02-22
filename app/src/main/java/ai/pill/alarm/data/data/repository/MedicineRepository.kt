package ai.pill.alarm.data.data.repository

import ai.pill.alarm.data.data.local.MedicineDao
import ai.pill.alarm.data.data.local.MedicineEntity
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val dao: MedicineDao) {

    // 1. Get the stream of all medicines from the database
    val allMedicines: Flow<List<MedicineEntity>> = dao.getAllMedicines()

    // 2. Insert a new medicine (runs on a background thread automatically)
    suspend fun insertMedicine(medicine: MedicineEntity) {
        dao.insertMedicine(medicine)
    }

    // 3. Delete a medicine
    suspend fun deleteMedicine(medicine: MedicineEntity) {
        dao.deleteMedicine(medicine)
    }
}