package ai.pill.alarm.userinterface

import ai.pill.alarm.data.data.local.MedicineEntity
import ai.pill.alarm.data.data.repository.MedicineRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import android.content.Context
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import androidx.core.net.toUri

class HomeViewModel(
    private val repository: MedicineRepository,
    private val alarmScheduler: AlarmScheduler // <-- 1. NEW: We brought the brain in!
) : ViewModel() {

    val allMedicines: Flow<List<MedicineEntity>> = repository.getAllMedicines()

    fun addMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            // We just insert here. Alarms are added later from the Home Screen!
            repository.insertMedicine(medicine)
        }
    }

    fun updateMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.updateMedicine(medicine)
            // <-- 2. NEW: The moment alarms are saved to the DB, schedule them in Android!
            alarmScheduler.scheduleAlarmsForMedicine(medicine)
        }
    }

    fun deleteMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            // <-- 3. NEW: Cancel the alarms BEFORE we delete the pill from the DB!
            alarmScheduler.cancelAlarmsForMedicine(medicine)
            repository.deleteMedicine(medicine)
        }
    }

    // --- GEMINI AI INTEGRATION ---
    fun generateAIInsights(context: Context, medicine: MedicineEntity) {
        if (medicine.imageUriAsString.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                // IMPORTANT: Keep your API key here!
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "YOUR_GEMINI_API_KEY"
                )

                val uri = medicine.imageUriAsString.toUri()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val prompt = """
                        You are a highly intelligent medical assistant. Look at this image of a medication.
                        Please provide:
                        1. The likely name of the medication.
                        2. A brief 1-sentence description of what it is generally used for.
                        3. Exactly 2 or 3 bullet points of common precautions or side effects.
                        Keep it extremely concise and easy to read. 
                        Start your response with a clear disclaimer: "⚕️ AI Estimation (Consult a doctor):"
                    """.trimIndent()

                    val response = generativeModel.generateContent(
                        content {
                            image(bitmap)
                            text(prompt)
                        }
                    )

                    val aiText = response.text ?: "Could not generate insights."
                    val updatedMedicine = medicine.copy(aiAdvice = aiText)

                    repository.updateMedicine(updatedMedicine)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMedicine = medicine.copy(aiAdvice = "Error analyzing image. Please ensure you have internet access and try again.")
                repository.updateMedicine(errorMedicine)
            }
        }
    }
}

// --- FACTORY ---
class HomeViewModelFactory(
    private val repository: MedicineRepository,
    private val alarmScheduler: AlarmScheduler // <-- 4. NEW: Tell the factory about the scheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}