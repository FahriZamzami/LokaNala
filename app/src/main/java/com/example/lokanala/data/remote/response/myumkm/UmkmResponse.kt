package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

data class UmkmResponse(
    @SerializedName("id_umkm") val id: Int,
    @SerializedName("nama_umkm") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("no_telepon") val noTelepon: String?,
    @SerializedName("gambar") val gambarUrl: String?,
    @SerializedName("rating") val rating: Double?
)