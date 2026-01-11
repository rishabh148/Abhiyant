package com.example.abhiyant.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.abhiyant.data.model.InspectionEntity
import com.example.abhiyant.data.model.InspectionStatus
import com.example.abhiyant.ui.viewmodel.InspectionListViewModel
import com.example.abhiyant.ui.viewmodel.SyncStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionListScreen(
    onNavigateToEntry: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: InspectionListViewModel = hiltViewModel()
) {
    val inspections by viewModel.inspections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null)}
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchInspections(searchQuery)
        } else {
            viewModel.loadInspections()
        }
    }
    
    LaunchedEffect(selectedStatusFilter) {
        selectedStatusFilter?.let { status ->
            viewModel.filterByStatus(status)
        } ?: viewModel.loadInspections()
    }
    
    LaunchedEffect(syncStatus) {
        if (syncStatus is SyncStatus.Success || 
            syncStatus is SyncStatus.Error) {
            // Reload inspections after sync
            viewModel.loadInspections()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inspection Reports") },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { viewModel.syncToCloud() }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Sync to Cloud")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Inspection")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search by name, part number, batch, serial...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }
            
            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All") }
                )
                InspectionStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status.name,
                        onClick = { selectedStatusFilter = status.name },
                        label = { Text(status.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sync Status Snackbar
            when (val status = syncStatus) {
                is SyncStatus.Syncing -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is SyncStatus.Success -> {
                    LaunchedEffect(status) {
                        viewModel.clearSyncStatus()
                    }
                }
                is SyncStatus.Error -> {
                    LaunchedEffect(status) {
                        viewModel.clearSyncStatus()
                    }
                }
                else -> {}
            }
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Inspection List
            if (inspections.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No inspections found. Tap + to add one.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inspections, key = { it.id }) { inspection ->
                        InspectionItemCard(
                            inspection = inspection,
                            onClick = { onNavigateToDetail(inspection.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionItemCard(
    inspection: InspectionEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = inspection.componentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    inspection.componentPartNumber?.let {
                        Text(
                            text = "Part: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusChip(status = inspection.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Inspector: ${inspection.inspectorName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = dateFormat.format(Date(inspection.inspectionDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!inspection.isSynced) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "Not Synced",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            inspection.batchNumber?.let {
                Text(
                    text = "Batch: $it",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: InspectionStatus) {
    val (color, textColor) = when (status) {
        InspectionStatus.PASSED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        InspectionStatus.FAILED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        InspectionStatus.NEEDS_REWORK -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        InspectionStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

