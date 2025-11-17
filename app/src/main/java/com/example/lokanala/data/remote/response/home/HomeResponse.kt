package com.example.lokanala.data.remote.response.home

data class HomeResponse(
    val success: Boolean,
    val data: List<HomeUMKMItem>
)

data class HomeUMKMItem(
    val id_umkm: Int,
    val id_user: Int,
    val id_kategori_umkm: Int?,
    val nama_umkm: String,
    val alamat: String?,
    val no_telepon: String?,
    val deskripsi: String?,
    val link_lokasi: String?,
    val tanggal_terdaftar: String?,
    val user: HomeUser?,
    val kategori_umkm: HomeKategoriUMKM?,
    val produk: List<HomeProdukItem>?,
    val promo: List<HomePromoItem>?
)

data class HomeUser(
    val id_user: Int,
    val nama: String,
    val email: String,
    val no_telepon: String,
    val foto_profile: String?
)

data class HomeKategoriUMKM(
    val id_kategori_umkm: Int,
    val nama_kategori: String,
    val deskripsi: String?
)

data class HomeProdukItem(
    val id_produk: Int,
    val id_umkm: Int,
    val id_kategori_produk: Int?,
    val nama_produk: String,
    val deskripsi: String?,
    val harga: Double,
    val gambar: String?,
    val tanggal_ditambahkan: String?
)

data class HomePromoItem(
    val id_promo: Int,
    val id_umkm: Int,
    val nama_promo: String,
    val deskripsi: String?,
    val syarat_penggunaan: String?,
    val tanggal_mulai: String?,
    val tanggal_berakhir: String?
)
