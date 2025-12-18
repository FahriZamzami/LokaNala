package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

// Request body untuk update urutan kategori
data class UpdateCategoryOrderRequest(
    @SerializedName("urutan")
    val urutan: List<CategoryOrderItem>
)

// Item urutan kategori
data class CategoryOrderItem(
    @SerializedName("id_kategori_produk")
    val idKategoriProduk: Int,

    @SerializedName("urutan")
    val urutan: Int
)

// Response untuk update urutan kategori
data class UpdateCategoryOrderResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UpdateCategoryOrderData?
)

data class UpdateCategoryOrderData(
    @SerializedName("umkmId")
    val umkmId: Int,

    @SerializedName("totalUpdated")
    val totalUpdated: Int
)

