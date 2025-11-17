package com.example.lokanala.data.remote.response

data class RatingRequest(
    val id_produk: Int,
    val id_user: Int,
    val komentar: String,
    val nilai_rating: Int
)
