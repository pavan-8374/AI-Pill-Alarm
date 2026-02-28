package ai.pill.alarm.data.data.local

import ai.pill.alarm.userinterface.AlarmSchedule
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters // We need to import the Room annotation

/**
 * A standard Kotlin Data Class used to hold our custom schedule information.
 * This is NOT a database table. It is just an object holding a String and a List.
 */
data class AlarmSchedule(val time: String, val days: List<Int>)

/**
 * @Entity tells Room: "Create a physical SQLite database table for this class."
 * tableName = "medicines" means the table will be named "medicines" instead of "MedicineEntity".
 * * @TypeConverters tells Room: "Whenever you see a data type in this table that you don't
 * understand (like our List<AlarmSchedule>), look inside the 'Converters' class to
 * find out how to translate it into a JSON string."
 */
@Entity(tableName = "medicines")
@TypeConverters(Converters::class) // This guarantees Room finds your translator!
data class MedicineEntity(

    /**
     * @PrimaryKey means this is the unique identifier for the row (like a barcode).
     * autoGenerate = true means you don't have to provide the ID yourself.
     * Room will automatically assign 1 to the first pill, 2 to the second, etc.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Standard text columns in the database
    val name: String,
    val instructions: String,

    // We save the URI (the file path to the secure image) as a simple String
    val imageUriAsString: String?,

    // Room doesn't natively understand Lists, which is why we needed the @TypeConverters above!
    val schedules: List<AlarmSchedule> = emptyList(),

    // A placeholder for the AI features you plan to add later
    val aiAdvice: String? = null
)