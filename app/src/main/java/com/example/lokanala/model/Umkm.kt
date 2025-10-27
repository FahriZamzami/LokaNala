package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class Umkm(
    val id: Int,
    val name: String,
    val rating: Double,
    val tag: String,
    @DrawableRes val imageRes: Int,
    // Kita tambahkan rute tujuan agar setiap card bisa dikonfigurasi
    val navigationRoute: String
)