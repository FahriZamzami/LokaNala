package com.example.lokanala.data.remote.response_and_request.login

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