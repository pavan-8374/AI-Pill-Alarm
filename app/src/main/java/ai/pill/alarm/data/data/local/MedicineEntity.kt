package ai.pill.alarm.data.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room will automatically count 1, 2, 3...

    val name: String,
    val timeString: String, // e.g., "08:00 AM"
    val instruction: String, // e.g., "Take with food"

    // We will save the AI's dietary advice here later!
    val aiAdvice: String? = null
)