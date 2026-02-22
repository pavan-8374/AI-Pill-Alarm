package ai.pill.alarm.data.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    // Insert a new medicine. If one with the same ID exists, replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    // Delete a medicine
    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    // Get ALL medicines.
    // Returning a 'Flow' means the UI will automatically update whenever the database changes!
    @Query("SELECT * FROM medicines ORDER BY timeString ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>
}