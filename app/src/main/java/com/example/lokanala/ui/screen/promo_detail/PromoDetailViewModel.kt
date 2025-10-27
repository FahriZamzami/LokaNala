package com.example.lokanala.ui.screen.promo_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lokanala.model.Promo
import com.example.lokanala.model.dummyPromos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// PERBAIKAN: Membuat ViewModel untuk mengambil detail promo berdasarkan ID
class PromoDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val promoId: Int = checkNotNull(savedStateHandle["promoId"])

    private val _promo = MutableStateFlow(
        dummyPromos.first { it.id == promoId }
    )
    val promo: StateFlow<Promo> = _promo.asStateFlow()
}
