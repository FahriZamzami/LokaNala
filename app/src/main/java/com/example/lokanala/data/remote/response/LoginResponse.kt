package com.example.lokanala.data.remote.response

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserData?
)

data class UserData(
    val id_user: Int,
    val nama: String,
    val email: String,
    val role: String
)
