package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

data class UmkmResponse(
    @SerializedName("id_umkm")
    val id: Int, // ID biasanya tidak boleh null

    @SerializedName("nama_umkm")
    val nama: String?, // Tambahkan ? (Boleh Null)

    @SerializedName("deskripsi")
    val deskripsi: String?, // Tambahkan ?

    @SerializedName("alamat")
    val alamat: String?, // Tambahkan ?

    @SerializedName("no_telepon")
    val noTelepon: String?, // Tambahkan ?

    @SerializedName("gambar")
    val gambarUrl: String?, // Tambahkan ?

    @SerializedName("rating")
    val rating: Double? // Tambahkan ?
)