package com.example.lokanala.data.remote.response

import com.example.lokanala.model.MyUmkm

data class MyUmkmResponse(
    val success: Boolean,
    val data: List<MyUmkm>
)