package com.example.abhiyant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abhiyant.data.cloud.CloudStorageService
import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.data.repository.InspectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InspectionListViewModel @Inject constructor(
    private val repository: InspectionRepository,
    private val cloudStorageService: CloudStorageService
) : ViewModel() {
    
    private val _inspections = MutableStateFlow<List<InspectionEntity>>(emptyList())
    val inspections: StateFlow<List<InspectionEntity>> = _inspections.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    init {
        loadInspections()
    }
    
    fun loadInspections() {
        viewModelScope.launch {
            repository.getAllInspections()
                .catch { e ->
                    _errorMessage.value = "Error loading inspections: ${e.message}"
                }
                .collect { inspectionList ->
                    _inspections.value = inspectionList
                }
        }
    }
    
    fun searchInspections(query: String) {
        if (query.isBlank()) {
            loadInspections()
            return
        }
        
        viewModelScope.launch {
            repository.searchInspections(query)
                .catch { e ->
                    _errorMessage.value = "Error searching inspections: ${e.message}"
                }
                .collect { inspectionList ->
                    _inspections.value = inspectionList
                }
        }
    }
    
    fun filterByStatus(status: String) {
        viewModelScope.launch {
            repository.getInspectionsByStatus(status)
                .catch { e ->
                    _errorMessage.value = "Error filtering inspections: ${e.message}"
                }
                .collect { inspectionList ->
                    _inspections.value = inspectionList
                }
        }
    }
    
    fun deleteInspection(inspection: InspectionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteInspection(inspection)
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting inspection: ${e.message}"
            }
        }
    }
    
    fun syncToCloud() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Syncing
            try {
                val unsyncedInspections = repository.getUnsyncedInspections()
                if (unsyncedInspections.isEmpty()) {
                    _syncStatus.value = SyncStatus.Success("All inspections are already synced")
                    return@launch
                }
                
                val result = cloudStorageService.uploadInspections(unsyncedInspections)
                result.fold(
                    onSuccess = { count ->
                        // Mark all as synced
                        unsyncedInspections.forEach { inspection ->
                            repository.markAsSynced(inspection.id, System.currentTimeMillis())
                        }
                        _syncStatus.value = SyncStatus.Success("Successfully synced $count inspection(s)")
                    },
                    onFailure = { exception ->
                        _syncStatus.value = SyncStatus.Error("Sync failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error("Error during sync: ${e.message}")
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun clearSyncStatus() {
        _syncStatus.value = SyncStatus.Idle
    }
}

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(val message: String) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

