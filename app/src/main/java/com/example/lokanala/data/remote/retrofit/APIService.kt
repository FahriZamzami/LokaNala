package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.UmkmResponse
import com.example.lokanala.data.remote.response.home.HomeResponse
import com.example.lokanala.data.remote.response.login.LoginRequest
import com.example.lokanala.data.remote.response.login.LoginResponse
import com.example.lokanala.data.remote.response.merchant.MerchantResponse
import com.example.lokanala.data.remote.response.product.ProductDetailResponse
import com.example.lokanala.data.remote.response.rating.AddReviewResponse
import com.example.lokanala.data.remote.response.rating.ReviewListResponse
import com.example.lokanala.model.Umkm
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("user/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("rating/{id_produk}")
    fun getRatingByProduct(
        @Path("id_produk") idProduk: Int
    ): Call<ReviewListResponse>

    @Multipart
    @POST("rating")
    fun addRating(
        @Part("id_produk") idProduk: RequestBody,
        @Part("id_user") idUser: RequestBody,
        @Part("nilai_rating") rating: RequestBody,
        @Part("komentar") komentar: RequestBody,
        @Part foto: List<MultipartBody.Part>
    ): Call<AddReviewResponse>

    @Multipart
    @PUT("rating/{id_rating}")
    fun updateRating(
        @Path("id_rating") idRating: Int,
        @Part("id_user") idUser: RequestBody,
        @Part("nilai_rating") rating: RequestBody,
        @Part("komentar") komentar: RequestBody,
        @Part keep_photos: List<MultipartBody.Part>,
        @Part foto: List<MultipartBody.Part>
    ): Call<AddReviewResponse>

    @DELETE("rating/{id_rating}")
    fun deleteRating(
        @Path("id_rating") idRating: Int,
        @Query("id_user") idUser: Int
    ): Call<Unit>

    @GET("umkm")
    fun getAllUmkm(): Call<HomeResponse>

    @GET("merchant/{id}")
    fun getMerchantDetail(@Path("id") id: Long): Call<MerchantResponse>

    @GET("umkm/{id}")
    suspend fun getUmkmDetail(@Path("id") umkmId: Int): Response<Umkm> // Added

    @GET("produk/{id}")
    fun getProductDetail(@Path("id") id: Int): Call<ProductDetailResponse>

    @GET("umkm/my")
    suspend fun getMyUmkm(
        @Query("id_user") userId: Int
    ): Response<List<UmkmResponse>>

}