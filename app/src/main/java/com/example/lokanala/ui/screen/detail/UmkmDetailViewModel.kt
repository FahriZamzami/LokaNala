package com.example.lokanala.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.R
import com.example.lokanala.model.UMKMDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// NAMA CLASS BARU
class UmkmDetailViewModel : ViewModel() {

    // NAMA STATE BARU
    private val _uiState = MutableStateFlow<UmkmDetailUiState>(UmkmDetailUiState.Loading)
    val uiState: StateFlow<UmkmDetailUiState> = _uiState

    fun getUmkmDetailById(umkmId: Long) {
        viewModelScope.launch {
            _uiState.value = UmkmDetailUiState.Loading // Gunakan state baru
            try {
                val detail = allUmkmDetails.find { it.id == umkmId }
                if (detail != null) {
                    _uiState.value = UmkmDetailUiState.Success(detail) // Gunakan state baru
                } else {
                    _uiState.value = UmkmDetailUiState.Error("Data tidak ditemukan") // Gunakan state baru
                }
            } catch (e: Exception) {
                _uiState.value = UmkmDetailUiState.Error(e.message ?: "Terjadi error") // Gunakan state baru
            }
        }
    }

    companion object {
        private val allUmkmDetails = listOf(
            UMKMDetail(
                id = 1,
                logoRes = R.drawable.logo_seblak_sendik, // Pastikan Anda punya logo_seblak_sendik.png di drawable
                name = "Seblak Sendik",
                description = "Seblak Sendik adalah usaha kuliner lokal yang menyajikan berbagai varian seblak khas Bandung dengan cita rasa pedas gurih yang menggugah selera. Tersedia topping beragam seperti ceker, sosis, bakso, dan kerupuk renyah yang dimasak langsung saat dipesan.",
                address = "Jl. Limau Manis No. 23, Padang Utara, Kota Padang, Sumatera Barat",
                contact = "0812-3456-7890 (WhatsApp & Telepon)",
                promos = listOf(
                    "Promo Pedas Level 5! Dapatkan diskon 10% untuk semua menu pedas level 5.",
                    "Paket Hemat: Seblak Ceker + Es Teh Manis hanya Rp20.000."
                )
            ),
            // Tambahkan data dummy untuk ID 2, 3, 4, ...
        )
    }
}

// NAMA SEALED CLASS BARU
sealed class UmkmDetailUiState {
    object Loading : UmkmDetailUiState()
    data class Success(val umkmDetail: UMKMDetail) : UmkmDetailUiState()
    data class Error(val message: String) : UmkmDetailUiState()
}