package com.example.abhiyant.data.dao

import androidx.room.*
import com.example.abhiyant.data.model.InspectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    
    @Query("SELECT * FROM inspections ORDER BY inspectionDate DESC")
    fun getAllInspections(): Flow<List<InspectionEntity>>
    
    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getInspectionById(id: Long): InspectionEntity?
    
    @Query("SELECT * FROM inspections WHERE componentName LIKE :searchQuery OR componentPartNumber LIKE :searchQuery OR batchNumber LIKE :searchQuery OR serialNumber LIKE :searchQuery ORDER BY inspectionDate DESC")
    fun searchInspections(searchQuery: String): Flow<List<InspectionEntity>>
    
    @Query("SELECT * FROM inspections WHERE isSynced = 0 ORDER BY inspectionDate DESC")
    suspend fun getUnsyncedInspections(): List<InspectionEntity>
    
    @Query("SELECT * FROM inspections WHERE status = :status ORDER BY inspectionDate DESC")
    fun getInspectionsByStatus(status: String): Flow<List<InspectionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionEntity): Long
    
    @Update
    suspend fun updateInspection(inspection: InspectionEntity)
    
    @Delete
    suspend fun deleteInspection(inspection: InspectionEntity)
    
    @Query("DELETE FROM inspections WHERE id = :id")
    suspend fun deleteInspectionById(id: Long)
    
    @Query("UPDATE inspections SET isSynced = 1, cloudSyncTimestamp = :timestamp WHERE id = :id")
    suspend fun markAsSynced(id: Long, timestamp: Long)
}

