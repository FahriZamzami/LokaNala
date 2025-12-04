package com.example.lokanala.model

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

data class MyUmkm(
    @SerializedName("id_umkm") val id_umkm: Int,
    @SerializedName("id_user") val id_user: Int,
    @SerializedName("id_kategori_umkm") val id_kategori: Int,
    val nama_umkm: String,
    val alamat: String,
    val no_telepon: String,
    val deskripsi: String,
    val link_lokasi: String,
    val gambar: String
)