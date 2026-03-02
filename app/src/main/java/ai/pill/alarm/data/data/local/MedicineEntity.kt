package ai.pill.alarm.data.data.local

import ai.pill.alarm.userinterface.AlarmSchedule // <-- Keep this import!
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Entity tells Room: "Create a physical SQLite database table for this class."
 * tableName = "medicines" means the table will be named "medicines".
 */
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val instructions: String,
    val imageUriAsString: String?,

    // Room doesn't natively understand Lists, so it will look for your Converters class
    val schedules: List<AlarmSchedule> = emptyList(),

    // A placeholder for the AI features
    val aiAdvice: String? = null
)