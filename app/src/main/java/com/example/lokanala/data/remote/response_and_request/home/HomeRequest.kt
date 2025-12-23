package com.example.lokanala.data.remote.response_and_request.home

data class Umkm(
    val id: Int,
    val name: String,
    val rating: Double = 0.0, 
    val tag: String,    
    val imageUrl: String?, 
    val description: String?,
    val tanggalTerdaftar: String, 
    val reviewCount: Int = 0 
)
