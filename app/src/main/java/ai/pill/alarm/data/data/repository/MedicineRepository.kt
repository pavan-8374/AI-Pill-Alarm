package ai.pill.alarm.data.data.repository

import ai.pill.alarm.data.data.local.MedicineDao
import ai.pill.alarm.data.data.local.MedicineEntity
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val dao: MedicineDao) {

    // Grabs the open pipe of medicines from the DAO
    fun getAllMedicines(): Flow<List<MedicineEntity>> {
        return dao.getAllMedicines()
    }

    // Tells the DAO to save a new medicine
    suspend fun insertMedicine(medicine: MedicineEntity) {
        dao.insertMedicine(medicine)
    }

    // 3. Delete a medicine
    suspend fun deleteMedicine(medicine: MedicineEntity) {
        dao.deleteMedicine(medicine)
    }
}