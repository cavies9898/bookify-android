package com.cavies.bookify.ui.screen.client.bookinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import com.cavies.bookify.ui.component.BookingCard
import com.cavies.bookify.ui.component.BookingFilter
import com.cavies.bookify.ui.component.EmptyStateView
import com.cavies.bookify.ui.component.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingListScreen(
    navController: androidx.navigation.NavController,
    viewModel: BookingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf(BookingFilter.ALL) }

    Box(Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                item {
                    BookingFilterChips(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = filter
                            viewModel.setFilter(filter)
                        }
                    )
                }
                if (uiState.bookings.isEmpty() && !uiState.isLoading) {
                    item {
                        BookingsEmptyState()
                    }
                } else {
                    items(uiState.bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            serviceName = uiState.serviceNames[booking.serviceId]
                                ?: stringResource(R.string.client_booking_list_default_service),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    navController.navigate("client_booking_detail/${booking.id}")
                                }
                        )
                    }
                }
            }
        }
        LoadingOverlay(isLoading = uiState.isLoading && uiState.bookings.isEmpty())
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookingFilterChips(
    selectedFilter: BookingFilter,
    onFilterSelected: (BookingFilter) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BookingFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(stringResource(filter.clientLabelRes)) }
            )
        }
    }
}

@Composable
private fun BookingsEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        EmptyStateView(
            icon = Icons.Default.EventBusy,
            title = stringResource(R.string.client_booking_list_empty_title),
            message = stringResource(R.string.client_booking_list_empty_message)
        )
    }
}
