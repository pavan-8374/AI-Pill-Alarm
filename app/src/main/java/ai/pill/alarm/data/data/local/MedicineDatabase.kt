package ai.pill.alarm.data.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/**
 * UNDERSTANDING THE DATABASE ANNOTATIONS:
 * @Database tells Android this is the main hub for your local data.
 * @TypeConverters(Converters::class) is crucial! It tells the database:
 * "Before you complain about not understanding a data type, go check the
 * Converters class we built to see if there is a translation available."
 */
@Database(
    entities = [MedicineEntity::class],
    version = 2, // We bumped this to 2 when we added the complex schedules
    exportSchema = false
)
@TypeConverters(Converters::class) // <-- THIS LINKS THE TRANSLATOR TO THE DATABASE
abstract class MedicineDatabase : RoomDatabase() {

    // This exposes your DAO (the SQL queries) to the rest of the app
    abstract val dao: MedicineDao

    // Companion objects act like 'static' elements in Java.
    // They belong to the class itself, not an instance of the class.
    companion object {

        // @Volatile means changes made by one thread are immediately visible to all other threads.
        // This prevents two different parts of your app from accidentally opening
        // two separate database connections at the exact same time.
        @Volatile
        private var INSTANCE: MedicineDatabase? = null

        fun getInstance(context: Context): MedicineDatabase {
            // If INSTANCE is not null, return it.
            // If it IS null, enter the synchronized block to build it safely.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDatabase::class.java,
                    "medicine_db"
                )
                    .fallbackToDestructiveMigration() // Wipes old table data when I change the version number
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}