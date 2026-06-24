package io.funstop.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.funstop.model.entity.EventLog

@Dao
interface EventDao {

    @Insert
    suspend fun insertEventLog(eventLog: EventLog)

    @Query("select * from user_event where isSynced = 0")
    suspend fun getAllEvent():List<EventLog>

    @Query("update user_event set isSynced = 1 where id = :id")
    suspend fun updateEventLog(id: Int)

}