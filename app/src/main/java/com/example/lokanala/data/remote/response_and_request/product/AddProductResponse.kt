package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String

    // Kita tidak perlu mengambil object 'data' di sini
    // karena biasanya setelah add sukses, kita hanya butuh statusnya saja.
)