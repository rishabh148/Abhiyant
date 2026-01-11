package com.example.abhiyant.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.data.repository.InspectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InspectionDetailViewModel @Inject constructor(
    private val repository: InspectionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val inspectionId: Long? = savedStateHandle.get<Long>("inspectionId")?.takeIf { it > 0 }
    
    private val _inspection = MutableStateFlow<InspectionEntity?>(null)
    val inspection: StateFlow<InspectionEntity?> = _inspection.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()
    
    private val _savedInspectionId = MutableStateFlow<Long?>(null)
    val savedInspectionId: StateFlow<Long?> = _savedInspectionId.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        inspectionId?.let { loadInspection(it) }
    }
    
    private fun loadInspection(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val inspection = repository.getInspectionById(id)
                _inspection.value = inspection
            } catch (e: Exception) {
                _errorMessage.value = "Error loading inspection: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveInspection(inspection: InspectionEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveStatus.value = SaveStatus.Saving
            try {
                val savedId = if (inspection.id == 0L) {
                    // New inspection - insertInspection returns the generated ID
                    val newId = repository.insertInspection(inspection)
                    newId
                } else {
                    // Update existing
                    repository.updateInspection(inspection)
                    inspection.id
                }
                _savedInspectionId.value = savedId
                _saveStatus.value = SaveStatus.Success("Inspection saved successfully")
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error("Error saving inspection: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteInspection(inspection: InspectionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteInspection(inspection)
                _saveStatus.value = SaveStatus.Success("Inspection deleted successfully")
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting inspection: ${e.message}"
            }
        }
    }
    
    fun clearSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Saving : SaveStatus()
    data class Success(val message: String) : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}

