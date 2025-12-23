package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName


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
    val urutan: Int = 0  
)


data class CategoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CategoryItem>
)


data class SingleCategoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CategoryItem
)


data class CreateCategoryRequest(
    @SerializedName("id_umkm")
    val umkmId: Int,

    @SerializedName("nama_kategori")
    val name: String,

    @SerializedName("deskripsi")
    val description: String?
)


data class UpdateCategoryRequest(
    @SerializedName("nama_kategori")
    val name: String,

    @SerializedName("deskripsi")
    val description: String?
)