package com.example.lokanala.data.remote.response.home

data class Umkm(
    val id: Int,
    val name: String,
    val rating: Double, // Note: Di controller belum ada kalkulasi rating, kita set default dulu
    val tag: String,    // Ini akan diambil dari kategori_umkm
    val imageUrl: String?, // Ubah dari imageRes: Int ke String?
    val description: String?
)
