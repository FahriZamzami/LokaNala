package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.login.LoginRequest
import com.example.lokanala.data.remote.response.login.LoginResponse
import com.example.lokanala.data.remote.response.home.HomeResponse
import com.example.lokanala.data.remote.response.merchant.MerchantResponse
import com.example.lokanala.data.remote.response.product.ProductDetailResponse
import com.example.lokanala.data.remote.response.rating.AddReviewResponse
import com.example.lokanala.data.remote.response.rating.ReviewListResponse
// UpdateReviewRequest tidak lagi dibutuhkan di sini karena kita ganti jadi Multipart

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ... (Login User tetap sama) ...
    @POST("user/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    // -------------------- RATING / ULASAN -------------------- //

    @GET("rating/{id_produk}")
    fun getRatingByProduct(
        @Path("id_produk") idProduk: Int
    ): Call<ReviewListResponse>

    // 1. POST Tambah Ulasan
    @Multipart
    @POST("rating")
    fun addRating(
        @Part("id_produk") idProduk: RequestBody,
        @Part("id_user") idUser: RequestBody,
        @Part("nilai_rating") rating: RequestBody,
        @Part("komentar") komentar: RequestBody,
        @Part foto: List<MultipartBody.Part>
    ): Call<AddReviewResponse>

    // 2. PUT Update Ulasan
    @Multipart
    @PUT("rating/{id_rating}")
    fun updateRating(
        @Path("id_rating") idRating: Int,
        // TAMBAHKAN INI: Backend butuh id_user untuk validasi kepemilikan
        @Part("id_user") idUser: RequestBody,
        @Part("nilai_rating") rating: RequestBody,
        @Part("komentar") komentar: RequestBody,
        @Part keep_photos: List<MultipartBody.Part>,
        @Part foto: List<MultipartBody.Part>
    ): Call<AddReviewResponse>

    // 3. DELETE Hapus Ulasan
    // Gunakan @Query agar URL menjadi: /rating/123?id_user=5
    @DELETE("rating/{id_rating}")
    fun deleteRating(
        @Path("id_rating") idRating: Int,
        @Query("id_user") idUser: Int // <--- TAMBAHKAN INI
    ): Call<Unit>

    // ... (Endpoint UMKM, Merchant, Product tetap sama) ...
    @GET("umkm")
    fun getAllUmkm(): Call<HomeResponse>

    @GET("merchant/{id}")
    fun getMerchantDetail(@Path("id") id: Long): Call<MerchantResponse>

    @GET("produk/{id}")
    fun getProductDetail(@Path("id") id: Int): Call<ProductDetailResponse>
}