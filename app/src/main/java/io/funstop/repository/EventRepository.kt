package io.funstop.repository

import io.funstop.dao.EventDao
import io.funstop.model.entity.EventLog
import javax.inject.Inject

class EventRepository @Inject constructor(private val eventDao: EventDao) {
    suspend fun logEvent(type:String,meta:String?){
        eventDao.insertEventLog(
            EventLog(
                eventType = type,
                meta = meta,
                timeStamp = System.currentTimeMillis().toLong(),
                isSynced = false
            )
        )
    }

    suspend fun getEvents() = eventDao.getAllEvent()

    suspend fun updateEventLog(id: Int) = eventDao.updateEventLog(id)
}