package io.funstop.database

import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import io.funstop.dao.EventDao
import io.funstop.dao.ProductDao
import io.funstop.model.entity.EventLog
import io.funstop.model.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, EventLog::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao

    abstract fun eventDao(): EventDao
}
