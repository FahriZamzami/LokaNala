package com.example.lokanala.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("msg")
    val message: String?,
    
    @SerializedName("message")
    val messageAlt: String?,
    
    @SerializedName("error")
    val error: String?
) {
    fun getErrorMessage(): String {
        return message ?: messageAlt ?: error ?: "Terjadi kesalahan"
    }
}

