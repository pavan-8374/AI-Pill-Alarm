package ai.pill.alarm.data.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ai.pill.alarm.userinterface.AlarmSchedule

// Room Database Type Converter
/**
 * UNDERSTANDING TYPE CONVERTERS:
 * Room Database uses SQLite. SQLite is very basic; it only knows how to save
 * primitive data types like Text (String), Whole Numbers (Int), and Decimals (Float).
 * * It has no idea what a "List<AlarmSchedule>" is.
 * * This Converters class acts as a dictionary. It takes your complex List, uses
 * Google's 'Gson' library to turn it into a single JSON String, and saves that String.
 * When you open the app later, it reads the String and translates it back into a List!
 */
class Converters {

    // Gson is the tool that handles the JSON text translation
    private val gson = Gson()

    /**
     * @TypeConverter is the magic tag. It tells the KSP compiler:
     * "Hey, if you ever need to save a List<AlarmSchedule>, use this exact function!"
     */
    @TypeConverter
    fun fromScheduleList(value: List<AlarmSchedule>): String {
        // Turns your List into a String like: "[{"time":"08:00 AM", "days":[1,3,5]}]"
        return gson.toJson(value)
    }

    /**
     * This second @TypeConverter tells the KSP compiler:
     * "When reading that JSON String back out of the database, use this function
     * to rebuild it into a Kotlin List<AlarmSchedule>."
     */
    @TypeConverter
    fun toScheduleList(value: String): List<AlarmSchedule> {
        // TypeToken is just a fancy way of telling Gson exactly what shape the data should take
        val listType = object : TypeToken<List<AlarmSchedule>>() {}.type

        // Translate the String back to a List. If it fails (null), return an emptyList() to prevent crashes.
        return gson.fromJson(value, listType) ?: emptyList()
    }
}