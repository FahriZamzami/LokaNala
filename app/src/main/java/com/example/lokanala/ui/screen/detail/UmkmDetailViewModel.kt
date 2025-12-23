package com.example.lokanala.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.R
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.UMKMDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UmkmDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UmkmDetailUiState>(UmkmDetailUiState.Loading)
    val uiState: StateFlow<UmkmDetailUiState> = _uiState

    fun getUmkmDetailById(umkmId: Long) {
        viewModelScope.launch {
            _uiState.value = UmkmDetailUiState.Loading
            try {
                
                val response = ApiClient.instance.getUmkmById(umkmId.toInt())

                if (response.isSuccessful && response.body() != null) {
                    val apiData = response.body()!!.data
                    if (apiData != null) {
                        val detail = UMKMDetail(
                            id = apiData.id.toLong(),
                            name = apiData.nama ?: "Tanpa Nama", 
                            description = apiData.deskripsi ?: "",
                            address = apiData.alamat ?: "",
                            contact = apiData.noTelepon ?: "",
                            logoRes = 0,
                            gambarUrl = apiData.gambarUrl, 
                            promos = emptyList(),
                            linkLokasi = apiData.linkLokasi
                        )
                        _uiState.value = UmkmDetailUiState.Success(detail)
                    }
                } else {
                    _uiState.value = UmkmDetailUiState.Error("UMKM dengan ID $umkmId tidak ditemukan di server")
                }
            } catch (e: Exception) {
                _uiState.value = UmkmDetailUiState.Error("Koneksi gagal: ${e.message}")
            }
        }
    }
}

sealed class UmkmDetailUiState {
    object Loading : UmkmDetailUiState()
    data class Success(val umkmDetail: UMKMDetail) : UmkmDetailUiState()
    data class Error(val message: String) : UmkmDetailUiState()
}