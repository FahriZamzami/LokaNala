package com.example.lokanala.data.remote.response_and_request.login

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String? = null
)