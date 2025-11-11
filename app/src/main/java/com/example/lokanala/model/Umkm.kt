package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class Umkm(
    val id: Int,
    val name: String,
    val rating: Double,
    val tag: String,
    @DrawableRes val imageRes: Int,
    val navigationRoute: String
)