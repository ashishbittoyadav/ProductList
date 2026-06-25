package io.funstop.work_manager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.funstop.dao.EventDao
import io.funstop.database.AppDatabase
import io.funstop.repository.EventRepository
import javax.inject.Inject

@HiltWorker
class EventUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: EventRepository
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        val events = repository.getEvents()

        Log.d("Data.TAG", "doWork: ${events.size}")
        if (events.isEmpty()) return Result.success()

        try {
            // api.uploadEvents(events)

            events.forEach { event ->
                repository.updateEventLog(event.id)
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}