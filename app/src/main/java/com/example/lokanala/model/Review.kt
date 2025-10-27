package com.example.lokanala.model

import androidx.annotation.DrawableRes

// PERBAIKAN: Menstandarkan model data Review untuk menggunakan drawable resource
data class Review(
    val id: Int,
    val name: String,
    val rating: Int,
    val date: String,
    val comment: String,
    @DrawableRes val profilePicRes: Int // Menggunakan Int untuk drawable resource
)
