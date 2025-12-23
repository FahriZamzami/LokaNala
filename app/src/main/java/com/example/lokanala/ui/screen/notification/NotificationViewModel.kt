package com.example.lokanala.ui.screen.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.ErrorResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.model.NotificationItem
import com.example.lokanala.data.remote.response_and_request.notification.NotificationResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NotificationViewModel(private val pref: UserPreference) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        Log.d("NotificationViewModel", "ViewModel initialized, loading notifications...")
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val user = pref.getSession().first()
                Log.d("NotificationViewModel", "Loading notifications for user ID: ${user.idUser}")

                if (user.idUser == -1 || user.token.isEmpty()) {
                    Log.w("NotificationViewModel", "User not logged in: idUser=${user.idUser}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User belum login"
                    )
                    return@launch
                }

                val authToken = if (user.token.startsWith("Bearer ")) {
                    user.token
                } else {
                    "Bearer ${user.token}"
                }

                Log.d("NotificationViewModel", "Calling endpoint: /user/notifications")
                val response = ApiClient.instance.getUserNotifications(authToken)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val notificationList = responseBody.data ?: emptyList()
                        Log.d("NotificationViewModel", "Success! Found ${notificationList.size} notifications")

                        val mappedNotifications = notificationList.map { apiItem ->
                            NotificationItem(
                                id = apiItem.idNotifikasi,
                                title = apiItem.judul,
                                description = apiItem.isi,
                                productName = ""
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notifications = mappedNotifications
                        )
                    } else {
                        Log.w("NotificationViewModel", "Response not successful: ${responseBody?.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = responseBody?.message ?: "Gagal memuat notifikasi"
                        )
                    }
                } else {
                    val errorBodyString = try {
                        response.errorBody()?.string() ?: ""
                    } catch (e: Exception) {
                        Log.w("NotificationViewModel", "Cannot read error body: ${e.message}")
                        ""
                    }

                    Log.e("NotificationViewModel", "API Error ${response.code()}: ${response.message()}")
                    Log.e("NotificationViewModel", "Error body: $errorBodyString")

                    val serverErrorMessage = try {
                        if (errorBodyString.isNotEmpty()) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorResponse.getErrorMessage()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.w("NotificationViewModel", "Cannot parse error JSON: ${e.message}")
                        null
                    }

                    val errorMessage = when (response.code()) {
                        500 -> {
                            serverErrorMessage?.let {
                                "Server error: $it"
                            } ?: "Server mengalami masalah. Silakan coba lagi nanti atau hubungi administrator."
                        }
                        404 -> "Endpoint tidak ditemukan. Pastikan aplikasi sudah diperbarui."
                        401, 403 -> "Akses ditolak. Silakan login ulang."
                        else -> {
                            serverErrorMessage ?: "Gagal memuat notifikasi (${response.code()}): ${response.message()}"
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }

            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error loading notifications", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                    e.message?.contains("timeout") == true ->
                        "Koneksi timeout. Silakan coba lagi."
                    else -> "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }
}