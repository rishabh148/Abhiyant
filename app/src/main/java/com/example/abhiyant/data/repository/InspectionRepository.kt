package com.example.abhiyant.data.repository

import com.example.abhiyant.data.dao.InspectionDao
import com.example.abhiyant.data.model.InspectionEntity
import kotlinx.coroutines.flow.Flow

class InspectionRepository(
    private val inspectionDao: InspectionDao
) {
    fun getAllInspections(): Flow<List<InspectionEntity>> = inspectionDao.getAllInspections()
    
    suspend fun getInspectionById(id: Long): InspectionEntity? = inspectionDao.getInspectionById(id)
    
    fun searchInspections(query: String): Flow<List<InspectionEntity>> {
        val searchQuery = "%$query%"
        return inspectionDao.searchInspections(searchQuery)
    }
    
    suspend fun getUnsyncedInspections(): List<InspectionEntity> = 
        inspectionDao.getUnsyncedInspections()
    
    fun getInspectionsByStatus(status: String): Flow<List<InspectionEntity>> = 
        inspectionDao.getInspectionsByStatus(status)
    
    suspend fun insertInspection(inspection: InspectionEntity): Long = 
        inspectionDao.insertInspection(inspection)
    
    suspend fun updateInspection(inspection: InspectionEntity) = 
        inspectionDao.updateInspection(inspection)
    
    suspend fun deleteInspection(inspection: InspectionEntity) = 
        inspectionDao.deleteInspection(inspection)
    
    suspend fun deleteInspectionById(id: Long) = 
        inspectionDao.deleteInspectionById(id)
    
    suspend fun markAsSynced(id: Long, timestamp: Long) = 
        inspectionDao.markAsSynced(id, timestamp)
}

