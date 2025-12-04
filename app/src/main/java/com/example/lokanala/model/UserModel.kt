package com.example.lokanala.model

data class UserModel(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val token: String = "",
    val isLogin: Boolean = false
)