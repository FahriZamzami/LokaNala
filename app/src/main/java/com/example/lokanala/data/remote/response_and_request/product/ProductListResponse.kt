package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<ProductItemDto>
)

data class ProductItemDto(
    @SerializedName("id_produk") val idProduk: Int,
    @SerializedName("nama_produk") val namaProduk: String,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("harga") val harga: Double,
    @SerializedName("gambar") val gambar: String?,
    @SerializedName("kategori_produk") val kategoriProduk: ProductCategoryDto?,
    @SerializedName("jumlah_ulasan") val jumlahUlasan: Int?,
    @SerializedName("rating_rata_rata") val ratingRataRata: Double?
)

data class ProductCategoryDto(
    @SerializedName("id_kategori_produk") val idKategoriProduk: Int?,
    @SerializedName("nama_kategori") val namaKategori: String?,
    @SerializedName("deskripsi") val deskripsi: String?
)