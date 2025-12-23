package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName

data class MerchantDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: MerchantData?
)

data class MerchantData(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("alamat") val alamat: String?,       
    @SerializedName("link_lokasi") val linkLokasi: String?, 
    @SerializedName("rating") val rating: Double?,       
    @SerializedName("kategori") val kategori: String?,   
    @SerializedName("products") val products: List<ProductItemResponse> = emptyList(),
    @field:SerializedName(value = "gambar_url") val gambar: String?,
)

data class ProductItemResponse(
    @SerializedName("id_produk") val idProduk: Int,
    @SerializedName("nama_produk") val namaProduk: String,
    @SerializedName("harga") val harga: Double,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("gambar") val gambar: String?,
    @SerializedName("kategori_produk") val kategoriProduk: String?,
    @SerializedName("jumlah_ulasan") val jumlahUlasan: Int?
)