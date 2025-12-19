package com.example.lokanala.data.remote.response_and_request.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserData?
)

data class UserData(
    val id_user: Int,
    val nama: String,
    val email: String,
    val no_telepon: String,
    val foto_profile: String?,
    val tanggal_dibuat: String
)

//data class LogoutResponse(
//    @SerializedName("success")
//    val success: Boolean,
//
//    @SerializedName("message")
//    val message: String
//)