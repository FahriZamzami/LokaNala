package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

// Model untuk satu item kategori (digunakan di List dan UI)
data class CategoryItem(
    @SerializedName("id_kategori_produk")
    val id: Int,

    @SerializedName("id_umkm")
    val umkmId: Int,

    @SerializedName("nama_kategori")
    val name: String,

    @SerializedName("deskripsi")
    val description: String?,

    @SerializedName("urutan")
    val urutan: Int = 0  // Urutan tampilan kategori (default 0 untuk backward compatibility)
)

// Response saat mengambil list kategori (GET)
data class CategoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CategoryItem>
)

// Response saat Create/Update kategori (Single data)
data class SingleCategoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CategoryItem
)

// Body untuk request CREATE
data class CreateCategoryRequest(
    @SerializedName("id_umkm")
    val umkmId: Int,

    @SerializedName("nama_kategori")
    val name: String,

    @SerializedName("deskripsi")
    val description: String?
)

// Body untuk request UPDATE
data class UpdateCategoryRequest(
    @SerializedName("nama_kategori")
    val name: String,

    @SerializedName("deskripsi")
    val description: String?
)