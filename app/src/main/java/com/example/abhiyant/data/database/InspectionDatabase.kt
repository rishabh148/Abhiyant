package com.example.abhiyant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.abhiyant.data.dao.InspectionDao
import com.example.abhiyant.data.model.InspectionEntity

@Database(
    entities = [InspectionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class InspectionDatabase : RoomDatabase() {
    
    abstract fun inspectionDao(): InspectionDao
}

