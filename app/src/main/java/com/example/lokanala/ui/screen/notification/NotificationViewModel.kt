package com.example.lokanala.ui.screen.notification

import androidx.lifecycle.ViewModel
import com.example.lokanala.model.NotificationItem
import com.example.lokanala.model.dummyNotifications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList()
)

class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        _uiState.value = NotificationUiState(notifications = dummyNotifications)
    }
}