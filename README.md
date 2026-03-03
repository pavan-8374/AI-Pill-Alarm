#  AI Pill Alarm  
This Appliction is Secure. Intelligent. Reliable. An Android application built with Jetpack Compose and Gemini AI to help users manage medications safely and never miss a dose.
# Key Features of AI PILL ALARM
### AI Medication Insights: 
Snap a photo of your pill bottle, and Gemini 1.5 Flash identifies the medication and provides concise safety precautions.
### Biometric Privacy: 
Secure access via Android's BiometricPrompt API (Fingerprint/Face Unlock).
### Full-Screen Alarms: 
Uses AlarmManager and Full-Screen Intents to wake the device and show a high-priority ringing screen with the pill image—even when the phone is locked. 
### Interactive UI: 
3D flipping medicine cards that separate schedule viewing from medication management.
### Offline Storage: 
Room Database created for fast, reliable local storage and this safe to store users medication in thier mobile.
## Technical StackLanguage: 
### 100% KotlinUI Framework: Jetpack Compose (Declarative UI)
### Architecture: MVVM (Model-View-ViewModel)
### AI Engine: Google Generative AI SDK (Gemini API)
### Local DB: Room Database (SQLite)
### Image Loading: Coil (with Coroutine support)
### Dependency Injection: Manual Injection (optimized for simplicity)
### Navigation: Jetpack Compose Navigation
# Project Architecture
The project follows clean architecture principles to ensure scalability and testability:
Data Layer: Room DAO for SQLite operations and a Repository pattern to abstract data sources.
Domain Layer: Custom TypeConverters to handle complex List<AlarmSchedule> objects within SQLite.
UI Layer: State-driven Composables that react to LiveData/Flow from the ViewModel.
System Layer: BroadcastReceiver and AlarmManager integration for system-level scheduling.

# Prerequisites
Android Studio Ladybug or newer.
An API Key from Google AI Studio.
A physical Android device (for Biometrics and Camera testing).

# Installation
Clone the repo:Bashgit clone 
Add your Gemini API Key: Open HomeViewModel.kt and replace the placeholder with your key:
KotlinapiKey = "YOUR_GEMINI_API_KEY"
Build and Run:Click the Run button in Android Studio.
 

## Permissions Required To function correctly, the app utilizes the following permissions:
#### INTERNET: To communicate with Gemini AI.
#### CAMERA: To capture medication images.
#### SCHEDULE_EXACT_ALARM: To trigger reminders at precise times.
#### USE_FULL_SCREEN_INTENT: To show the ringing screen over the lock screen.
#### POST_NOTIFICATIONS: For Android 13+ status bar alerts.

## App Screenshots
<img width="1080" height="2424" alt="Splash Screen" src="https://github.com/user-attachments/assets/3c0cef64-96f0-46ad-8108-300f339f7b3c" />
<img width="1080" height="2424" alt="Schedule" src="https://github.com/user-attachments/assets/fa17a6a1-1679-40b6-9b41-0da576d0ac66" />
<img width="1080" height="2424" alt="My medicines" src="https://github.com/user-attachments/assets/2ee91cb1-9c37-48ae-8ce2-b1907c67c2b4" />
<img width="1080" height="2424" alt="Delete" src="https://github.com/user-attachments/assets/72553ded-7a4d-48d9-a4e5-e869aad82939" />
<img width="1080" height="2424" alt="Capture image" src="https://github.com/user-attachments/assets/8557c9c3-b235-4400-9496-14616df2edc2" />
<img width="1080" height="2424" alt="Application logo" src="https://github.com/user-attachments/assets/1205032a-284a-407d-8974-60934283dbe8" />
<img width="1080" height="2424" alt="Alarm ring" src="https://github.com/user-attachments/assets/d0eb9b7e-2979-46b7-b91c-96b917be6dcf" />
<img width="1080" height="2424" alt="Add new pill" src="https://github.com/user-attachments/assets/8d9d75ac-0db5-4371-af32-71b0cb4f1707" />


# Developed By 
### Rallapalli Pavan
