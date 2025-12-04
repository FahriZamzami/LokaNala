package com.example.lokanala.ui.screen.myumkm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.UmkmResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk UI
data class MyUmkmUiState(
    val isLoading: Boolean = false,
    val myUmkmList: List<UmkmResponse> = emptyList(),
    val errorMessage: String? = null
)

class MyUmkmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyUmkmUiState())
    val uiState: StateFlow<MyUmkmUiState> = _uiState.asStateFlow()

    // TODO: Sebaiknya userId diambil dari DataStore/Session Manager
    // Untuk sementara kita hardcode atau set dari activity
    private var currentUserId: Int = 1

    // Tag untuk memudahkan pencarian di Logcat
    private val TAG = "MyUmkmViewModel"

    init {
        loadMyUmkm()
    }

    fun setUserId(id: Int) {
        Log.d(TAG, "setUserId: Mengganti user ID dari $currentUserId menjadi $id")
        currentUserId = id
        loadMyUmkm()
    }

    fun loadMyUmkm() {
        viewModelScope.launch {
            Log.d(TAG, "loadMyUmkm: Memulai proses ambil data untuk User ID: $currentUserId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Panggil API
                Log.d(TAG, "loadMyUmkm: Menghubungi API...")
                val response = ApiClient.instance.getMyUmkm(currentUserId)
                Log.d(TAG, "loadMyUmkm: Respon diterima. Kode: ${response.code()}")

                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    Log.d(TAG, "loadMyUmkm: Sukses! Jumlah data: ${data.size}")
                    // Log data pertama untuk debug (jika ada)
                    if (data.isNotEmpty()) {
                        Log.d(TAG, "loadMyUmkm: Contoh data pertama: ${data[0]}")
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        myUmkmList = data
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = "Gagal memuat data: ${response.message()} (Code: ${response.code()})"
                    Log.e(TAG, "loadMyUmkm: API Error. $msg")
                    Log.e(TAG, "loadMyUmkm: Error Body: $errorBody")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = msg
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadMyUmkm: Exception Terjadi!", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }
}