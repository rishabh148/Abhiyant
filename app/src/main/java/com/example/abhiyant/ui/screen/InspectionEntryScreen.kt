package com.example.abhiyant.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.data.model.InspectionStatus
import com.example.abhiyant.ui.viewmodel.InspectionDetailViewModel
import com.example.abhiyant.ui.viewmodel.SaveStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionEntryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: InspectionDetailViewModel = hiltViewModel()
) {
    val inspection by viewModel.inspection.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val savedInspectionId by viewModel.savedInspectionId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Form state
    var componentName by remember { mutableStateOf(inspection?.componentName ?: "") }
    var componentPartNumber by remember { mutableStateOf(inspection?.componentPartNumber ?: "") }
    var inspectorName by remember { mutableStateOf(inspection?.inspectorName ?: "") }
    var batchNumber by remember { mutableStateOf(inspection?.batchNumber ?: "") }
    var serialNumber by remember { mutableStateOf(inspection?.serialNumber ?: "") }
    
    // Vernier measurements
    var vernierLength by remember { mutableStateOf(inspection?.vernierLength?.toString() ?: "") }
    var vernierWidth by remember { mutableStateOf(inspection?.vernierWidth?.toString() ?: "") }
    var vernierHeight by remember { mutableStateOf(inspection?.vernierHeight?.toString() ?: "") }
    var vernierDiameter by remember { mutableStateOf(inspection?.vernierDiameter?.toString() ?: "") }
    
    // Micrometer measurements
    var micrometerThickness by remember { mutableStateOf(inspection?.micrometerThickness?.toString() ?: "") }
    var micrometerOuterDiameter by remember { mutableStateOf(inspection?.micrometerOuterDiameter?.toString() ?: "") }
    var micrometerInnerDiameter by remember { mutableStateOf(inspection?.micrometerInnerDiameter?.toString() ?: "") }
    
    // Digital height master
    var heightMasterMeasurement by remember { mutableStateOf(inspection?.heightMasterMeasurement?.toString() ?: "") }
    
    // Status and notes
    var selectedStatus by remember { mutableStateOf(inspection?.status ?: InspectionStatus.PENDING) }
    var notes by remember { mutableStateOf(inspection?.notes ?: "") }
    var remarks by remember { mutableStateOf(inspection?.remarks ?: "") }
    
    // Update form when inspection loads
    LaunchedEffect(inspection) {
        inspection?.let {
            componentName = it.componentName
            componentPartNumber = it.componentPartNumber ?: ""
            inspectorName = it.inspectorName
            batchNumber = it.batchNumber ?: ""
            serialNumber = it.serialNumber ?: ""
            vernierLength = it.vernierLength?.toString() ?: ""
            vernierWidth = it.vernierWidth?.toString() ?: ""
            vernierHeight = it.vernierHeight?.toString() ?: ""
            vernierDiameter = it.vernierDiameter?.toString() ?: ""
            micrometerThickness = it.micrometerThickness?.toString() ?: ""
            micrometerOuterDiameter = it.micrometerOuterDiameter?.toString() ?: ""
            micrometerInnerDiameter = it.micrometerInnerDiameter?.toString() ?: ""
            heightMasterMeasurement = it.heightMasterMeasurement?.toString() ?: ""
            selectedStatus = it.status
            notes = it.notes ?: ""
            remarks = it.remarks ?: ""
        }
    }
    
    // Handle save status
    LaunchedEffect(saveStatus, savedInspectionId) {
        when (val status = saveStatus) {
            is SaveStatus.Success -> {
                savedInspectionId?.let { id ->
                    if (id > 0) {
                        // Navigate to detail screen with the saved inspection ID
                        onNavigateToDetail(id)
                    } else {
                        // Navigate back if no ID
                        onNavigateBack()
                    }
                } ?: run {
                    // Navigate back if no saved ID
                    onNavigateBack()
                }
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (inspection != null) "Edit Inspection" else "New Inspection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val newInspection = InspectionEntity(
                        id = inspection?.id ?: 0L,
                        componentName = componentName,
                        componentPartNumber = componentPartNumber.takeIf { it.isNotBlank() },
                        inspectorName = inspectorName,
                        batchNumber = batchNumber.takeIf { it.isNotBlank() },
                        serialNumber = serialNumber.takeIf { it.isNotBlank() },
                        vernierLength = vernierLength.toDoubleOrNull(),
                        vernierWidth = vernierWidth.toDoubleOrNull(),
                        vernierHeight = vernierHeight.toDoubleOrNull(),
                        vernierDiameter = vernierDiameter.toDoubleOrNull(),
                        micrometerThickness = micrometerThickness.toDoubleOrNull(),
                        micrometerOuterDiameter = micrometerOuterDiameter.toDoubleOrNull(),
                        micrometerInnerDiameter = micrometerInnerDiameter.toDoubleOrNull(),
                        heightMasterMeasurement = heightMasterMeasurement.toDoubleOrNull(),
                        status = selectedStatus,
                        notes = notes.takeIf { it.isNotBlank() },
                        remarks = remarks.takeIf { it.isNotBlank() }
                    )
                    viewModel.saveInspection(newInspection)
                },
                icon = { Icon(Icons.Default.Save, contentDescription = null) },
                text = { Text("Save") },
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Information Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = componentName,
                        onValueChange = { componentName = it },
                        label = { Text("Component Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = componentPartNumber,
                        onValueChange = { componentPartNumber = it },
                        label = { Text("Part Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = inspectorName,
                        onValueChange = { inspectorName = it },
                        label = { Text("Inspector Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = batchNumber,
                            onValueChange = { batchNumber = it },
                            label = { Text("Batch Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
            
            // Vernier Measurements Section
            MeasurementCard(
                title = "Vernier Measurements (mm)",
                measurements = listOf(
                    MeasurementField("Length", vernierLength) { vernierLength = it },
                    MeasurementField("Width", vernierWidth) { vernierWidth = it },
                    MeasurementField("Height", vernierHeight) { vernierHeight = it },
                    MeasurementField("Diameter", vernierDiameter) { vernierDiameter = it }
                )
            )
            
            // Micrometer Measurements Section
            MeasurementCard(
                title = "Micrometer Measurements (mm)",
                measurements = listOf(
                    MeasurementField("Thickness", micrometerThickness) { micrometerThickness = it },
                    MeasurementField("Outer Diameter", micrometerOuterDiameter) { micrometerOuterDiameter = it },
                    MeasurementField("Inner Diameter", micrometerInnerDiameter) { micrometerInnerDiameter = it }
                )
            )
            
            // Digital Height Master Section
            MeasurementCard(
                title = "Digital Height Master (mm)",
                measurements = listOf(
                    MeasurementField("Measurement", heightMasterMeasurement) { heightMasterMeasurement = it }
                )
            )
            
            // Status Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    InspectionStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Text(
                                text = status.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Notes Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Notes & Remarks",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }
            
            // Save Status Messages
            when (val status = saveStatus) {
                is SaveStatus.Saving -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is SaveStatus.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = status.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MeasurementCard(
    title: String,
    measurements: List<MeasurementField>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            measurements.forEach { field ->
                OutlinedTextField(
                    value = field.value,
                    onValueChange = { field.onValueChange(it) },
                    label = { Text(field.label) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        }
    }
}

data class MeasurementField(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit
)

