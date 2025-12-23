package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName


data class UpdateCategoryOrderRequest(
    @SerializedName("urutan")
    val urutan: List<CategoryOrderItem>
)


data class CategoryOrderItem(
    @SerializedName("id_kategori_produk")
    val idKategoriProduk: Int,

    @SerializedName("urutan")
    val urutan: Int
)


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

