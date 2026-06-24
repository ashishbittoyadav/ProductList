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
