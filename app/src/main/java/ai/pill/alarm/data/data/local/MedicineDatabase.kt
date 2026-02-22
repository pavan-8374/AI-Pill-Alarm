package ai.pill.alarm.data.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MedicineEntity::class],
    version = 1, // Change this number if you ever add new columns to the table
    exportSchema = false
)
abstract class MedicineDatabase : RoomDatabase() {

    abstract val dao: MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: MedicineDatabase? = null

        // This ensures we only ever have ONE instance of the database open at a time
        fun getInstance(context: Context): MedicineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDatabase::class.java,
                    "medicine_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}