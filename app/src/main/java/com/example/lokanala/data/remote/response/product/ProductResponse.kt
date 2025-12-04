package com.example.lokanala.data.remote.response.product

import com.google.gson.annotations.SerializedName

// 1. Wrapper Utama
data class ProductDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProductDetailData?
)

// 2. Data Utama Produk
data class ProductDetailData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama_produk")
    val namaProduk: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("harga")
    val harga: Double,

    @SerializedName("gambar")
    val gambarUrl: String?,

    @SerializedName("rating_rata_rata")
    val rating: Double,

    @SerializedName("jumlah_ulasan")
    val jumlahUlasan: Int,

    @SerializedName("umkm")
    val umkm: UmkmShortData,

    @SerializedName("ulasan_terbaik")
    val ulasanTerbaik: TopReviewData? // Bisa null jika belum ada ulasan
)

// 3. Info Singkat UMKM (Pemilik Produk)
data class UmkmShortData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("logo")
    val logoUrl: String?
)

// 4. Info Ulasan Terbaik
data class TopReviewData(
    @SerializedName("id_ulasan")
    val idUlasan: Int,

    @SerializedName("nama_user")
    val namaUser: String,

    @SerializedName("foto_user")
    val fotoUserUrl: String?,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("komentar")
    val komentar: String?,

    @SerializedName("tanggal")
    val tanggal: String
)