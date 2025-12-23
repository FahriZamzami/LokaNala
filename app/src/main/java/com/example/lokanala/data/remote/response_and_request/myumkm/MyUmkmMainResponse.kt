package com.example.lokanala.data.remote.response_and_request

import com.google.gson.annotations.SerializedName

data class MyUmkmMainResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<UmkmResponse>
)