package io.funstop.application

import android.app.Application
import android.util.Log
import androidx.compose.ui.unit.Constraints
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import io.funstop.work_manager.EventUploadWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ProductApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val request = PeriodicWorkRequestBuilder<EventUploadWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(androidx.work.Constraints(requiresCharging = true, requiredNetworkType = NetworkType.UNMETERED))
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "event_upload",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}