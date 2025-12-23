package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName

data class UmkmResponse(
    @SerializedName("id_umkm")
    val id: Int,

    @SerializedName("id_kategori_umkm")
    val idKategoriUmkm: Int?,

    @SerializedName("nama_umkm")
    val nama: String?,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("alamat")
    val alamat: String?,

    @SerializedName("no_telepon")
    val noTelepon: String?,

    @SerializedName("gambar")
    val gambarUrl: String?,

    
    @SerializedName("rating")
    val rating: Double?,

    @SerializedName("total_ulasan")
    val totalUlasan: Int?,

    @SerializedName("link_lokasi")
    val linkLokasi: String?,
)

data class UmkmSingleResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: UmkmResponse? 
)


data class UmkmActionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)

data class UmkmRequest(
    val id_user: Int,
    val id_kategori_umkm: Int?,
    val nama_umkm: String,
    val alamat: String,
    val no_telepon: String,
    val deskripsi: String,
    val link_lokasi: String,
    val gambar: String? 
)

data class UMKMDetail(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val contact: String,
    val logoRes: Int = 0,
    val gambarUrl: String? = null, 
    val promos: List<String> = emptyList(),
    val linkLokasi: String? = null
)

data class KategoriUmkmItem(
    @SerializedName("id_kategori_umkm")
    val idKategoriUmkm: Int,

    @SerializedName("nama_kategori")
    val namaKategori: String,

    @SerializedName("deskripsi")
    val deskripsi: String?
)

data class KategoriUmkmResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<KategoriUmkmItem>?
)