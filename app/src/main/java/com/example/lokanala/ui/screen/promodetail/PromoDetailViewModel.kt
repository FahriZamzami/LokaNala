package com.example.lokanala.ui.screen.promodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lokanala.model.Promo
import com.example.lokanala.model.dummyPromos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PromoDetailUiState(
    val promo: Promo
)

class PromoDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<PromoDetailUiState>
    private val promoId: Int = checkNotNull(savedStateHandle["promoId"])

    init {
        // Cari promo berdasarkan ID, jika tidak ketemu, pakai yang pertama
        val promo = dummyPromos.find { it.id == promoId } ?: dummyPromos.first()
        _uiState = MutableStateFlow(PromoDetailUiState(promo))
    }

    val uiState: StateFlow<PromoDetailUiState> = _uiState.asStateFlow()
}