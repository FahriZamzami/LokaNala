package com.example.lokanala.model

import androidx.annotation.DrawableRes

// PERBAIKAN: Menambahkan kolom untuk detail promo
data class Promo(
    val id: Int,
    val title: String,
    val dateRange: String,
    val newPrice: String? = null,
    val oldPrice: String? = null,
    val hasMoreOptions: Boolean = false,
    @DrawableRes val imageResDetail: Int, // <-- Gambar untuk detail
    val termsAndConditions: List<String>, // <-- Syarat & Ketentuan
    val howToUse: List<String> // <-- Cara Penggunaan
)
