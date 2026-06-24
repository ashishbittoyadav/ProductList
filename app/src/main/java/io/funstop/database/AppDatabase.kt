package io.funstop.database

import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import io.funstop.dao.ProductDao
import io.funstop.model.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
}
