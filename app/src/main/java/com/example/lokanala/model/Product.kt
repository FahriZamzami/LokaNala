package com.example.lokanala.model

import androidx.annotation.DrawableRes

// PERBAIKAN: Menstandarkan model data Product untuk menggunakan drawable resource
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val rating: Double,
    val reviewCount: Int,
    @DrawableRes val imageRes: Int,
    @DrawableRes val imageResDetail: Int
)
