package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val rating: Double,
    val reviewCount: Int,
    val categoryName: String,
    @DrawableRes val imageRes: Int? = null,
    val imageUri: String? = null,
    @DrawableRes val imageResDetail: Int? = null,
    val imageDetailUri: String? = null
)