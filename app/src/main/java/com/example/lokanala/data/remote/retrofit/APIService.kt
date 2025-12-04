package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.LoginRequest
import com.example.lokanala.data.remote.response.LoginResponse
import com.example.lokanala.data.remote.response.KategoriResponse
import com.example.lokanala.data.remote.response.MyUmkmResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // LOGIN
    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // KATEGORI
    @GET("kategori")
    suspend fun getKategori(): KategoriResponse

}
