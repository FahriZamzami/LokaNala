package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

data class MyUmkmMainResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<UmkmResponse> // Data list ada di dalam field ini
)