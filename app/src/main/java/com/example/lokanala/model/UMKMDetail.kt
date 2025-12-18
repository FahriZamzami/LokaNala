package com.example.lokanala.model

import androidx.annotation.DrawableRes

data class UMKMDetail(
    val id: Long,
    @DrawableRes val logoRes: Int,
    val name: String,
    val description: String,
    val address: String,
    val contact: String,
    val promos: List<String>
)