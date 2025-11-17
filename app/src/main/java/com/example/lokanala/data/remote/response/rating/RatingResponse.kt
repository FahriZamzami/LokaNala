package com.example.lokanala.data.remote.response

import com.example.lokanala.data.remote.response.login.UserData

data class RatingResponse(
    val id_rating: Int,
    val id_produk: Int,
    val id_user: Int,
    val komentar: String,
    val nilai_rating: Int,
    val tanggal_dibuat: String,
    val user: UserData? // opsional, bisa tampilkan nama & foto user
)
