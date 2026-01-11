package com.example.abhiyant.data.cloud

import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.data.model.InspectionStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import java.util.Date

class CloudStorageService(
    private val firestore: FirebaseFirestore,
    private val gson: Gson
) {
    
    companion object {
        private const val COLLECTION_INSPECTIONS = "inspections"
    }
    
    suspend fun uploadInspection(inspection: InspectionEntity): Result<String> {
        return try {
            val inspectionMap = inspectionToMap(inspection)
            val docRef = firestore.collection(COLLECTION_INSPECTIONS)
                .document(inspection.id.toString())
                .set(inspectionMap)
                .await()
            
            Result.success(inspection.id.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadInspections(inspections: List<InspectionEntity>): Result<Int> {
        return try {
            val batch = firestore.batch()
            var successCount = 0
            
            inspections.forEach { inspection ->
                val docRef = firestore.collection(COLLECTION_INSPECTIONS)
                    .document(inspection.id.toString())
                val inspectionMap = inspectionToMap(inspection)
                batch.set(docRef, inspectionMap)
                successCount++
            }
            
            batch.commit().await()
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun downloadInspections(): Result<List<InspectionEntity>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_INSPECTIONS)
                .orderBy("inspectionDate", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val inspections = snapshot.documents.mapNotNull { document ->
                try {
                    mapToInspection(document.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(inspections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteInspection(id: Long): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_INSPECTIONS)
                .document(id.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun inspectionToMap(inspection: InspectionEntity): Map<String, Any?> {
        return mapOf(
            "id" to inspection.id,
            "componentName" to inspection.componentName,
            "componentPartNumber" to inspection.componentPartNumber,
            "inspectionDate" to inspection.inspectionDate,
            "inspectorName" to inspection.inspectorName,
            "batchNumber" to inspection.batchNumber,
            "serialNumber" to inspection.serialNumber,
            "vernierLength" to inspection.vernierLength,
            "vernierWidth" to inspection.vernierWidth,
            "vernierHeight" to inspection.vernierHeight,
            "vernierDiameter" to inspection.vernierDiameter,
            "micrometerThickness" to inspection.micrometerThickness,
            "micrometerOuterDiameter" to inspection.micrometerOuterDiameter,
            "micrometerInnerDiameter" to inspection.micrometerInnerDiameter,
            "heightMasterMeasurement" to inspection.heightMasterMeasurement,
            "additionalMeasurements" to inspection.additionalMeasurements,
            "status" to (inspection.status.name),
            "isSynced" to inspection.isSynced,
            "cloudSyncTimestamp" to inspection.cloudSyncTimestamp,
            "notes" to inspection.notes,
            "remarks" to inspection.remarks
        )
    }
    
    private fun mapToInspection(map: Map<String, Any?>): InspectionEntity {
        return InspectionEntity(
            id = (map["id"] as? Number)?.toLong() ?: 0L,
            componentName = map["componentName"] as? String ?: "",
            componentPartNumber = map["componentPartNumber"] as? String,
            inspectionDate = (map["inspectionDate"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            inspectorName = map["inspectorName"] as? String ?: "",
            batchNumber = map["batchNumber"] as? String,
            serialNumber = map["serialNumber"] as? String,
            vernierLength = (map["vernierLength"] as? Number)?.toDouble(),
            vernierWidth = (map["vernierWidth"] as? Number)?.toDouble(),
            vernierHeight = (map["vernierHeight"] as? Number)?.toDouble(),
            vernierDiameter = (map["vernierDiameter"] as? Number)?.toDouble(),
            micrometerThickness = (map["micrometerThickness"] as? Number)?.toDouble(),
            micrometerOuterDiameter = (map["micrometerOuterDiameter"] as? Number)?.toDouble(),
            micrometerInnerDiameter = (map["micrometerInnerDiameter"] as? Number)?.toDouble(),
            heightMasterMeasurement = (map["heightMasterMeasurement"] as? Number)?.toDouble(),
            additionalMeasurements = map["additionalMeasurements"] as? String,
            status = try {
                InspectionStatus.valueOf(map["status"] as? String ?: "PENDING")
            } catch (e: IllegalArgumentException) {
                InspectionStatus.PENDING
            },
            isSynced = map["isSynced"] as? Boolean ?: false,
            cloudSyncTimestamp = (map["cloudSyncTimestamp"] as? Number)?.toLong(),
            notes = map["notes"] as? String,
            remarks = map["remarks"] as? String
        )
    }
}

