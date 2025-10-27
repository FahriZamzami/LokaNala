package com.example.lokanala.ui.screen.promo

import androidx.lifecycle.ViewModel
import com.example.lokanala.model.dummyPromos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// PERBAIKAN: Menggunakan UI State untuk menampung data Promo
class PromoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PromoUiState())
    val uiState: StateFlow<PromoUiState> = _uiState.asStateFlow()

    init {
        getPromos()
    }

    private fun getPromos() {
        _uiState.update {
            it.copy(promos = dummyPromos)
        }
    }
}
