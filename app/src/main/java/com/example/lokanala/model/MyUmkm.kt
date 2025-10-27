package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class MyUmkm(
    val id: Int,
    val name: String,
    val rating: Double,
    @DrawableRes val imageRes: Int
)