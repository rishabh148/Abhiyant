package com.example.abhiyant.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.ui.viewmodel.InspectionDetailViewModel
import com.example.abhiyant.ui.viewmodel.SaveStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: InspectionDetailViewModel = hiltViewModel()
) {
    val inspection by viewModel.inspection.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(saveStatus) {
        when (val status = saveStatus) {
            is SaveStatus.Success -> {
                if (status.message.contains("deleted", ignoreCase = true)) {
                    onNavigateBack()
                }
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inspection Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    inspection?.let { insp ->
                        IconButton(onClick = { onNavigateToEdit(insp.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (inspection == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Inspection not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                inspection?.let { insp ->
                    // Basic Information
                    DetailCard("Basic Information") {
                        DetailRow("Component Name", insp.componentName)
                        insp.componentPartNumber?.let { DetailRow("Part Number", it) }
                        DetailRow("Inspector Name", insp.inspectorName)
                        insp.batchNumber?.let { DetailRow("Batch Number", it) }
                        insp.serialNumber?.let { DetailRow("Serial Number", it) }
                        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                        DetailRow("Inspection Date", dateFormat.format(Date(insp.inspectionDate)))
                        DetailRow("Status", insp.status.name)
                    }
                    
                    // Vernier Measurements
                    if (insp.vernierLength != null || insp.vernierWidth != null || 
                        insp.vernierHeight != null || insp.vernierDiameter != null) {
                        DetailCard("Vernier Measurements (mm)") {
                            insp.vernierLength?.let { DetailRow("Length", "$it mm") }
                            insp.vernierWidth?.let { DetailRow("Width", "$it mm") }
                            insp.vernierHeight?.let { DetailRow("Height", "$it mm") }
                            insp.vernierDiameter?.let { DetailRow("Diameter", "$it mm") }
                        }
                    }
                    
                    // Micrometer Measurements
                    if (insp.micrometerThickness != null || insp.micrometerOuterDiameter != null || 
                        insp.micrometerInnerDiameter != null) {
                        DetailCard("Micrometer Measurements (mm)") {
                            insp.micrometerThickness?.let { DetailRow("Thickness", "$it mm") }
                            insp.micrometerOuterDiameter?.let { DetailRow("Outer Diameter", "$it mm") }
                            insp.micrometerInnerDiameter?.let { DetailRow("Inner Diameter", "$it mm") }
                        }
                    }
                    
                    // Digital Height Master
                    insp.heightMasterMeasurement?.let {
                        DetailCard("Digital Height Master (mm)") {
                            DetailRow("Measurement", "$it mm")
                        }
                    }
                    
                    // Notes & Remarks
                    if (insp.notes != null || insp.remarks != null) {
                        DetailCard("Notes & Remarks") {
                            insp.notes?.let { DetailRow("Notes", it) }
                            insp.remarks?.let { DetailRow("Remarks", it) }
                        }
                    }
                    
                    // Sync Status
                    DetailCard("Cloud Status") {
                        DetailRow("Synced", if (insp.isSynced) "Yes" else "No")
                        insp.cloudSyncTimestamp?.let {
                            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            DetailRow("Last Synced", dateFormat.format(Date(it)))
                        }
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Inspection") },
            text = { Text("Are you sure you want to delete this inspection? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        inspection?.let { viewModel.deleteInspection(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

