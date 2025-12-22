package com.example.lokanala.data.remote.response_and_request.following

import com.google.gson.annotations.SerializedName

data class FollowingResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<FollowingUmkmItem>?
)

data class FollowingUmkmItem(
    @SerializedName("id_umkm") val idUmkm: Int,
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("id_kategori_umkm") val idKategoriUmkm: Int?,
    @SerializedName("nama_umkm") val namaUmkm: String,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("no_telepon") val noTelepon: String?,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("gambar_url") val gambarUrl: String?,
    @SerializedName("link_lokasi") val linkLokasi: String?,
    @SerializedName("tanggal_terdaftar") val tanggalTerdaftar: String,
    @SerializedName("kategori_umkm") val kategori: KategoriUmkm?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("promos") val promos: List<MerchantPromo>?
)

data class KategoriUmkm(
    @SerializedName("id_kategori_umkm") val idKategori: Int,
    @SerializedName("nama_kategori") val namaKategori: String,
    @SerializedName("deskripsi") val deskripsi: String?
)

data class MerchantPromo(
    @SerializedName("id_promo") val idPromo: Int,
    @SerializedName("nama_promo") val namaPromo: String,
    @SerializedName("deskripsi") val deskripsi: String?
)

