package com.example.lokanala.ui.screen.promo

import com.example.lokanala.model.Promo

// PERBAIKAN: Membuat data class untuk UI State
data class PromoUiState(
    val promos: List<Promo> = emptyList()
)
