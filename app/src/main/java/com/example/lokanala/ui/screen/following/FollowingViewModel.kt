package com.example.lokanala.ui.screen.following

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.ErrorResponse
import com.example.lokanala.data.remote.response_and_request.following.FollowingResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.home.Umkm
import com.example.lokanala.data.remote.response_and_request.home.UmkmItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FollowingUiState(
    val umkmList: List<Umkm> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FollowingViewModel(private val pref: UserPreference) : ViewModel() {
    private val _uiState = MutableStateFlow(FollowingUiState())
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    init {
        loadFollowing()
    }

    fun loadFollowing() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val user = pref.getSession().first()
                Log.d("FollowingViewModel", "Loading following for user ID: ${user.idUser}")

                if (user.idUser == -1 || user.token.isEmpty()) {
                    Log.w("FollowingViewModel", "User not logged in")
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

                Log.d("FollowingViewModel", "Calling endpoint: /user/following")
                val response = ApiClient.instance.getUserFollowing(authToken)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val followingList = responseBody.data ?: emptyList()
                        Log.d("FollowingViewModel", "Success! Found ${followingList.size} following UMKM")

                        val mappedUmkmList = followingList.map { item ->
                            Umkm(
                                id = item.idUmkm,
                                name = item.namaUmkm,
                                rating = item.rating ?: 0.0,
                                tag = item.kategori?.namaKategori ?: "UMKM",
                                imageUrl = item.gambarUrl ?: item.linkLokasi,
                                description = item.deskripsi,
                                tanggalTerdaftar = item.tanggalTerdaftar,
                                reviewCount = 0 
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            umkmList = mappedUmkmList
                        )
                    } else {
                        Log.w("FollowingViewModel", "Response not successful: ${responseBody?.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = responseBody?.message ?: "Gagal memuat following"
                        )
                    }
                } else {
                    val errorBodyString = try {
                        response.errorBody()?.string() ?: ""
                    } catch (e: Exception) {
                        Log.w("FollowingViewModel", "Cannot read error body: ${e.message}")
                        ""
                    }

                    Log.e("FollowingViewModel", "API Error ${response.code()}: ${response.message()}")

                    val serverErrorMessage = try {
                        if (errorBodyString.isNotEmpty()) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorResponse.getErrorMessage()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    val errorMessage = when (response.code()) {
                        500 -> serverErrorMessage ?: "Server mengalami masalah. Silakan coba lagi nanti."
                        404 -> "Endpoint tidak ditemukan. Pastikan aplikasi sudah diperbarui."
                        401, 403 -> "Akses ditolak. Silakan login ulang."
                        else -> serverErrorMessage ?: "Gagal memuat following (${response.code()})"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }

            } catch (e: Exception) {
                Log.e("FollowingViewModel", "Error loading following", e)
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

