# Notebook App – CP5307 Zhou Jianqi Assessment 1

The Android notebook application built with **Jetpack Compose** and **Material Design 3**. This app allows users to create, edit, and manage notes with persistent storage, along with a famous quote feature powered by DummyJSON API.

---

## Core Functionality

### 1. Note Management
- **Create Notes**: Add new notes using the floating action button (+) in the bottom right corner
- **Edit Notes**: Tap on any note to open the editor and modify its content
- **Delete Notes**: Swipe left on a note entry to delete it with visual feedback. It's really convenient, isn't it?
- **Persistent Storage**: All notes are **automatic** stored locally using Room Database

### 2. famous Quotes
- **Quote Button**: Tap the "Q" button in the bottom left corner to fetch a random famous quote
- **External API Integration**: Quotes are fetched from DummyJSON API (https://dummyjson.com)
- **Copy to Clipboard**: Dialog displays the quote with options to copy it to clipboard or cancel
- **Loading Indicator**: Visual feedback while fetching quotes from the API

### 3. Theme Customization
- **Settings Screen**: Access theme customization through the bottom navigation bar
- **Four Theme Options**:
    - Taro Purple
    - Notebook Blue
    - Tomato Red
    - Night Sky Black
- **Customizable UI color**: Theme colors apply to the top bar, floating action buttons, and UI accents
- **Theme color retention**: The app automatically saves the current theme color option

### 4. Navigation
- **Bottom Navigation Bar**: Switch between Home and Settings screens
- **Persistent Navigation**: Navigation bar remains accessible across all major screens

---

## Technical Implementation

### Architecture & Components

#### Data Layer
- **Note.kt**: Room entity representing a note with id, title, and content
- **NoteDao.kt**: Data Access Object defining database operations (insert, update, delete, getAllNotes)
- **NoteDatabase.kt**: Room database singleton providing access to NoteDao

#### ViewModel Layer
- **NoteViewModel.kt**: AndroidViewModel managing note operations and exposing Flow of notes to the UI

#### UI Layer (Composables)
- **UtilityApp()**: Main container managing app state, navigation, and dialogs
- **HomeScreen()**: Displays note list with swipe-to-delete functionality using SwipeToDismissBox
- **EditNoteScreen()**: Full-screen editor for creating and modifying notes with BasicTextField
- **SettingsScreen()**: Theme selection interface with visual color options

#### Network Layer
- **RetrofitInstance**: Singleton Retrofit client configured for DummyJSON API
- **QuoteApi**: Retrofit interface defining the random quote endpoint
- **QuoteResponse**: Data class for deserializing API responses

---

## Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- Android device or emulator with API 24+ (API 36 recommended)
- Internet connection for quote feature

### How to Run
1. Clone this repository
2. Open the project in Android Studio
3. Wait for Gradle sync to complete
4. Run on an emulator or physical device

### Permissions
- `INTERNET`: Required for fetching quotes from DummyJSON API

---

## Key Features Implementation

### Note Persistence
- Uses Room Database for local SQLite storage
- Flow-based reactive updates ensure UI automatically reflects data changes
- Swipe-to-dismiss pattern with Material3 SwipeToDismissBox

### Quote Feature
- Asynchronous API calls using Kotlin coroutines
- Error handling with user-friendly fallback messages
- Clipboard integration for easy quote sharing
- Loading state with CircularProgressIndicator

### Theme System
- Runtime theme switching without app restart
- Color-coded UI elements respond to theme selection
- Persistent theme state using Compose state management
- Implement theme color state storage via SharedPreferences

---

## Dependencies

```kotlin
// Core Android & Compose
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.compose.bom)
implementation(libs.androidx.material3)

// Room Database
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
```

---

## 📚 License
This project is developed for educational purposes as part of CP5307 coursework.
