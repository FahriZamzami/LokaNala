package com.example.lokanala.data.remote.response_and_request.myumkm

import com.example.lokanala.data.remote.response_and_request.UmkmResponse
import com.google.gson.annotations.SerializedName

data class MyUmkmResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<UmkmResponse>?
)

