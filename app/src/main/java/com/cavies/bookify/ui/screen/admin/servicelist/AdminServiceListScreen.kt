package com.cavies.bookify.ui.screen.admin.servicelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cavies.bookify.R
import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.ui.component.EmptyStateView
import com.cavies.bookify.ui.component.LoadingOverlay
import com.cavies.bookify.ui.component.ServiceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceListScreen(
    navController: androidx.navigation.NavController,
    viewModel: AdminServiceListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

    serviceToDelete?.let { service ->
        DeleteServiceDialog(
            serviceName = service.name,
            onConfirm = {
                viewModel.deleteService(service.id)
                serviceToDelete = null
            },
            onDismiss = { serviceToDelete = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.services.isEmpty() && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyStateView(
                    icon = Icons.Default.EventBusy,
                    title = stringResource(R.string.admin_service_list_empty_title),
                    message = stringResource(R.string.admin_service_list_empty_message)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(uiState.services) { service ->
                    val dismissState = rememberSwipeToDismissBoxState()
                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            serviceToDelete = service
                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                    }
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.admin_service_list_cd_delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false
                    ) {
                        ServiceCard(
                            service = service,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
        LoadingOverlay(isLoading = uiState.isLoading && uiState.services.isEmpty())
    }
}

@Composable
private fun DeleteServiceDialog(
    serviceName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.admin_service_list_delete_title)) },
        text = {
            Text(stringResource(R.string.admin_service_list_delete_message, serviceName))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.admin_service_list_delete_confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.admin_service_list_delete_cancel))
            }
        }
    )
}
