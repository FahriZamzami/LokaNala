package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class Promo(
    val id: Int,
    val title: String,
    val dateRange: String,
    val newPrice: String? = null,
    val oldPrice: String? = null,
    val hasMoreOptions: Boolean = false,
    @DrawableRes val imageResDetail: Int,
    val termsAndConditions: List<String>,
    val howToUse: List<String>
)
