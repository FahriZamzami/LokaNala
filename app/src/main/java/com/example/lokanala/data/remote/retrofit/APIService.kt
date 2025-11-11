package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.LoginRequest
import com.example.lokanala.data.remote.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // ✅ BENAR - hapus "api/" di awal karena sudah ada di BASE_URL
    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}