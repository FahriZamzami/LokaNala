package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response.*
import com.example.lokanala.data.remote.response.home.HomeResponse
import com.example.lokanala.data.remote.response.myumkm.MyUmkmResponse
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

    // --- RATING ---
    @GET("rating/{id_produk}")
    fun getRatingByProduct(@Path("id_produk") idProduk: Int): Call<ReviewListResponse>

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
    fun deleteRating(@Path("id_rating") idRating: Int, @Query("id_user") idUser: Int): Call<Unit>

    // --- UMKM ---
    @GET("umkm")
    fun getAllUmkm(): Call<HomeResponse>
    
    // Suspend version for coroutines
    @GET("umkm")
    suspend fun getAllUmkmSuspend(): Response<HomeResponse>

    @GET("merchant/{id}")
    fun getMerchantDetail(@Path("id") id: Long): Call<MerchantResponse>

    // Versi Suspend untuk ViewModel (PENTING)
    @GET("merchant/{id}")
    suspend fun getMerchantDetailSuspend(@Path("id") id: Int): MerchantDetailResponse

    @GET("umkm/{id}")
    suspend fun getUmkmDetail(@Path("id") umkmId: Int): Response<Umkm>

    @GET("umkm/my")
    suspend fun getMyUmkmByUserId(@Query("id_user") userId: Int): Response<MyUmkmResponse>

    @GET("user/my-umkm")
    suspend fun getMyUmkmByToken(@Header("Authorization") token: String): MyUmkmMainResponse

    // --- PRODUK (PENTING UNTUK ViewModel) ---
    @GET("produk/{id}")
    fun getProductDetail(@Path("id") id: Int): Call<ProductDetailResponse>
    
    // Suspend version for coroutines
    @GET("produk/{id}")
    suspend fun getProductDetailSuspend(@Path("id") id: Int): Response<ProductDetailResponse>

    @GET("umkm/{umkmId}/produk")
    suspend fun getProductsByUmkm(@Path("umkmId") umkmId: Int): Response<ProductListResponse>

    @DELETE("produk/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<GeneralResponse>

    @Multipart
    @POST("produk")
    suspend fun addProduct(
        @Part("id_umkm") idUmkm: RequestBody,
        @Part("id_kategori_produk") idKategori: RequestBody,
        @Part("nama_produk") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Response<AddProductResponse>

    @Multipart
    @PUT("produk/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Part("id_umkm") idUmkm: RequestBody,
        @Part("id_kategori_produk") idKategori: RequestBody,
        @Part("nama_produk") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Response<AddProductResponse>

    // --- KATEGORI ---
    @GET("kategori-produk/umkm/{umkmId}")
    suspend fun getCategories(@Path("umkmId") umkmId: Int): Response<CategoryResponse>

    @POST("kategori-produk")
    suspend fun addCategory(@Body request: CreateCategoryRequest): Response<SingleCategoryResponse>

    @PUT("kategori-produk/{categoryId}")
    suspend fun updateCategory(@Path("categoryId") categoryId: Int, @Body request: UpdateCategoryRequest): Response<SingleCategoryResponse>

    @DELETE("kategori-produk/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Int): Response<Unit>

    // --- UPDATE URUTAN KATEGORI ---
    @PUT("kategori-produk/urutan/{umkmId}")
    suspend fun updateCategoryOrder(
        @Path("umkmId") umkmId: Int,
        @Body request: UpdateCategoryOrderRequest
    ): Response<UpdateCategoryOrderResponse>
}