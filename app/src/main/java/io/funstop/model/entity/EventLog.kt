package io.funstop.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_event")
data class EventLog (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val eventType:String,
    val timeStamp:Long,
    val meta: String?,
    val isSynced: Boolean
)