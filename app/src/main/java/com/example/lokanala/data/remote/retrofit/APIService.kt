package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.login.LoginRequest
import com.example.lokanala.data.remote.response.login.LoginResponse
import com.example.lokanala.data.remote.response.RatingRequest
import com.example.lokanala.data.remote.response.RatingResponse
import com.example.lokanala.data.remote.response.home.HomeResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // -------------------- USER -------------------- //
    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // -------------------- RATING -------------------- //
    @GET("rating/{id_produk}")
    suspend fun getRatingByProduct(
        @Path("id_produk") idProduk: Int
    ): Response<List<RatingResponse>>

    @POST("rating")
    suspend fun addRating(
        @Body request: RatingRequest
    ): Response<RatingResponse>

    @PUT("rating/{id_rating}")
    suspend fun updateRating(
        @Path("id_rating") idRating: Int,
        @Body request: RatingRequest
    ): Response<RatingResponse>

    @DELETE("rating/{id_rating}")
    suspend fun deleteRating(
        @Path("id_rating") idRating: Int
    ): Response<Unit>

    // -------------------- UMKM -------------------- //
    /**
     * GET semua UMKM (wrapper success + data)
     * Sesuai response controller:
     * { success: true, data: [...] }
     */
    @GET("umkm")
    suspend fun getAllUMKM(
    ): Response<HomeResponse>
}