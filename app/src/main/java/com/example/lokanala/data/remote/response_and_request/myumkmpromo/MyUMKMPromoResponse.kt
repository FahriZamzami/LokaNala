package com.example.lokanala.data.remote.response_and_request.myumkmpromo

data class PromoListResponse(
    val success: Boolean,
    val data: List<PromoItem>
)

data class PromoItem(
    val id_promo: Int,
    val id_umkm: Int,
    val nama_promo: String,
    val deskripsi: String?,
    val syarat_penggunaan: String?,
    val cara_penggunaan: String?,
    val tanggal_mulai: String?,
    val tanggal_berakhir: String?
)

data class PromoDetailResponse(
    val success: Boolean,
    val data: PromoItem
)

data class PromoCreateUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: PromoItem
)