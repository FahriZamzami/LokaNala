package com.example.lokanala.ui.screen.myumkm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.ErrorResponse
import com.example.lokanala.data.remote.response_and_request.UmkmResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MyUmkmUiState(
    val isLoading: Boolean = false,
    val myUmkmList: List<UmkmResponse> = emptyList(),
    val errorMessage: String? = null
)

class MyUmkmViewModel(private val pref: UserPreference) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUmkmUiState())
    val uiState: StateFlow<MyUmkmUiState> = _uiState.asStateFlow()

    init {
        loadMyUmkm()
    }

    fun loadMyUmkm() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val user = pref.getSession().first()
                Log.d("MyUmkmViewModel", "Loading UMKM for user ID: ${user.idUser}")

                // Cek apakah user sudah login
                if (user.idUser == -1) {
                    Log.w("MyUmkmViewModel", "User not logged in: idUser=${user.idUser}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User belum login"
                    )
                    return@launch
                }

                // Gunakan endpoint dengan userId
                Log.d("MyUmkmViewModel", "Calling endpoint: /umkm/my?id_user=${user.idUser}")
                val response = ApiClient.instance.getMyUmkmByUserId(user.idUser)
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val umkmList = responseBody.data ?: emptyList()
                        Log.d("MyUmkmViewModel", "Success! Found ${umkmList.size} UMKM")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            myUmkmList = umkmList
                        )
                    } else {
                        Log.w("MyUmkmViewModel", "Response not successful: ${responseBody?.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = responseBody?.message ?: "Gagal memuat data"
                        )
                    }
                } else {
                    // Baca error body untuk mendapatkan detail error dari server
                    val errorBodyString = try {
                        response.errorBody()?.string() ?: ""
                    } catch (e: Exception) {
                        Log.w("MyUmkmViewModel", "Cannot read error body: ${e.message}")
                        ""
                    }
                    
                    Log.e("MyUmkmViewModel", "API Error ${response.code()}: ${response.message()}")
                    Log.e("MyUmkmViewModel", "Error body: $errorBodyString")
                    
                    // Parse error message dari JSON jika ada
                    val serverErrorMessage = try {
                        if (errorBodyString.isNotEmpty()) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorResponse.getErrorMessage()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.w("MyUmkmViewModel", "Cannot parse error JSON: ${e.message}")
                        null
                    }
                    
                    val errorMessage = when (response.code()) {
                        500 -> {
                            // Untuk error 500, tampilkan pesan dari server jika ada, atau pesan default
                            serverErrorMessage?.let { 
                                "Server error: $it"
                            } ?: "Server mengalami masalah. Silakan coba lagi nanti atau hubungi administrator."
                        }
                        404 -> "Endpoint tidak ditemukan. Pastikan aplikasi sudah diperbarui."
                        401, 403 -> "Akses ditolak. Silakan login ulang."
                        else -> {
                            serverErrorMessage ?: "Gagal memuat data (${response.code()}): ${response.message()}"
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }

            } catch (e: Exception) {
                Log.e("MyUmkmViewModel", "Error loading UMKM", e)
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
