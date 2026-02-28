package ai.pill.alarm.data.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO stands for Data Access Object.
 * Think of this as the "Controller" or "Remote Control" for your database.
 * You don't write the complex SQL database logic yourself; instead, you
 * define Kotlin functions here, and Room automatically generates the SQL code for you!
 */
@Dao
interface MedicineDao {

    /**
     * @Insert tells Room: "Take this Kotlin object and add it as a new row in the database."
     * OnConflictStrategy.REPLACE means: If a pill with the exact same ID already exists,
     * overwrite the old one with this new data (great for editing/updating pills!).
     * * We use 'suspend' because saving to a database takes time. 'suspend' forces
     * this function to run on a background thread so it doesn't freeze the user's screen.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    /**
     * @Delete tells Room to find the matching row in the database and remove it.
     * Also uses 'suspend' to run in the background.
     */
    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    /**
     * @Query allows us to write raw SQL commands.
     * "SELECT * FROM medicines" means: "Get every single row from the 'medicines' table."
     * * Notice there is NO 'suspend' keyword here. Instead, it returns a 'Flow'.
     * A Flow is an active, open pipe between the database and your UI.
     * If you add a new pill, the database pushes the new list through this Flow
     * automatically, and your UI updates instantly without you having to ask for it again!
     */
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    // NOTE: We deleted the old getMedicineByTime() query because 'timeString'
    // no longer exists in our Entity!
}