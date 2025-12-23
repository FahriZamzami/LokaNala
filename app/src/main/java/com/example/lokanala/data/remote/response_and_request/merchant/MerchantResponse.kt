package com.example.lokanala.data.remote.response_and_request.merchant

import com.google.gson.annotations.SerializedName


data class MerchantResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: MerchantData?
)


data class MerchantData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("alamat")
    val alamat: String?,

    @SerializedName("gambar_url")
    val imageHeaderUrl: String?,

    @SerializedName("link_lokasi")
    val linkLokasi: String?, 

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("promos")
    val promos: List<MerchantPromo>,

    @SerializedName("products")
    val products: List<MerchantProduct>
)


data class MerchantPromo(
    @SerializedName("id_promo")
    val idPromo: Int,

    @SerializedName("nama_promo")
    val namaPromo: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("syarat")
    val syarat: String?,

    @SerializedName("berlaku_sampai")
    val berlakuSampai: String?
)


data class MerchantProduct(
    @SerializedName("id_produk")
    val idProduk: Int,

    @SerializedName("nama_produk")
    val namaProduk: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("harga")
    val harga: Double,

    @SerializedName("gambar_url")
    val gambarUrl: String?,

    @SerializedName("kategori_produk")
    val kategoriProduk: String,

    @SerializedName("rating_produk") 
    val ratingProduk: Double?,

    @SerializedName("jumlah_ulasan")
    val jumlahUlasan: Int
)