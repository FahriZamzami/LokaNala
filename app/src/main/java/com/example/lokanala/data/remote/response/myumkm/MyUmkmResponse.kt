package com.example.lokanala.data.remote.response.myumkm

import com.example.lokanala.data.remote.response.UmkmResponse
import com.google.gson.annotations.SerializedName

data class MyUmkmResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<UmkmResponse>?
)

