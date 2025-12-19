package com.example.lokanala.data.remote.response_and_request.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String? = null
)

//data class LogoutRequest(
//    @SerializedName("id_user")
//    val idUser: Int
//)