package com.example.lokanala.data.remote.response_and_request.myumkmpromo

data class CreatePromoRequest(
    val nama_promo: String,
    val deskripsi: String? = null,
    val syarat_penggunaan: String? = null,
    val cara_penggunaan: String? = null,
    val tanggal_mulai: String? = null,
    val tanggal_berakhir: String? = null
)

data class UpdatePromoRequest(
    val nama_promo: String,
    val deskripsi: String? = null,
    val syarat_penggunaan: String? = null,
    val cara_penggunaan: String? = null,
    val tanggal_mulai: String? = null,
    val tanggal_berakhir: String? = null,
)