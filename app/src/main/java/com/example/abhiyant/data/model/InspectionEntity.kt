package com.example.abhiyant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val componentName: String,
    val componentPartNumber: String? = null,
    val inspectionDate: Long = System.currentTimeMillis(),
    val inspectorName: String,
    val batchNumber: String? = null,
    val serialNumber: String? = null,
    
    // Vernier measurements
    val vernierLength: Double? = null,
    val vernierWidth: Double? = null,
    val vernierHeight: Double? = null,
    val vernierDiameter: Double? = null,
    
    // Micrometer measurements
    val micrometerThickness: Double? = null,
    val micrometerOuterDiameter: Double? = null,
    val micrometerInnerDiameter: Double? = null,
    
    // Digital height master measurements
    val heightMasterMeasurement: Double? = null,
    
    // Additional measurements (flexible for other tools)
    val additionalMeasurements: String? = null, // JSON string for flexible data
    
    // Inspection status
    val status: InspectionStatus = InspectionStatus.PENDING,
    
    // Cloud sync status
    val isSynced: Boolean = false,
    val cloudSyncTimestamp: Long? = null,
    
    // Notes and remarks
    val notes: String? = null,
    val remarks: String? = null
)

enum class InspectionStatus {
    PENDING,
    PASSED,
    FAILED,
    NEEDS_REWORK
}

