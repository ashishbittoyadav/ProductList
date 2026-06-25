# 📦 ProductList App

## 🏗️ Architecture Overview

This project follows a **clean architecture approach** with clear separation of concerns using:

* MVVM (Model-View-ViewModel)
* Repository pattern
* Room Database
* WorkManager for background tasks
* Hilt for Dependency Injection

---

## 📁 Project Structure

```
application     → App class & WorkManager configuration  
dao             → Room DAO interfaces (EventDao,ProductDao)  
database        → Room database setup (use entities ProductEntity and EventLog) 
di              → Dependency Injection (Hilt modules)  
mapper          → Data mapping (Product ↔ Domain ↔ ProductEntity)  
model           → Data models (DTOs, Entities)  
navigation      → Navigation components  
repository      → Data handling layer
screen          → UI screens (Compose)  
uiState         → UI state management  
viewModel       → Business logic layer  
work_manager    → Background workers (Upload event logs)
```

---

### Architectural Diagram
```
UI (Compose)
   ↓
ViewModel (StateFlow)
   ↓
Repository
   ↓        ↓
Room DB   Retrofit API
   ↓
WorkManager (Sync Worker)
```

## 📊 User Event Flow

### 🧭 Event Logging Flow

```
UI → ProductViewModel → EventRepository → EventDao → Room DB
```

* User actions are captured from the UI.
* Events are passed through the ViewModel to the Repository.
* Stored locally in Room database.
* Each event is saved with:

    * `isSynced = false`

---

### 🔄 Background Sync Flow

```
EventUploadWorker → EventRepository → EventDao → Room DB
```

* WorkManager triggers `EventUploadWorker`.
* Fetches all unsynced events (`isSynced = false`).
* Uploads events to the server.
* On success:
    * Updates events with `isSynced = true`
* Runs every 15 minutes
* Requires:
  * Unmetered network (Wi-Fi)
  * Device charging
* Automatically retries on failure

---

## ⚙️ Background Processing

* Implemented using **WorkManager**
* Supports:

    * Reliable execution
    * Automatic retries
    * Background constraints (network, battery)

---

## 🧠 Key Highlights

* Clean and scalable architecture
* Offline-first event tracking
* Background sync using WorkManager
* Dependency Injection with Hilt
* Modular and maintainable codebase

---

## 👨‍💻 Tech Stack

* Kotlin
* Jetpack Compose
* Room Database
* WorkManager
* Hilt (DI)
* Retrofit (Networking)

---

## ⏱ Flash Deal Countdown

- Countdown handled in ViewModel
- Uses Kotlin Flow emitting every 1 second
- Survives configuration changes
- Ensures no memory leaks

## 🚀 Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device
5. Internet required for initial data fetch

## 🗂️ Generate Apk
```
  ./gradlew assemble
  // for release and debug both
  ./gradlew assembleDebug 
  // for debug only
```

## 🏆 Why This Architecture?

- Scalable for large apps
- Easy testing
- Clear separation of concerns
- Supports offline-first apps