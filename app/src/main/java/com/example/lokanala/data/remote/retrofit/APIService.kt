package com.example.lokanala.data.remote.retrofit

import com.example.lokanala.data.remote.response_and_request.*
import com.example.lokanala.data.remote.response_and_request.following.FollowingResponse
import com.example.lokanala.data.remote.response_and_request.login.LoginRequest
import com.example.lokanala.data.remote.response_and_request.login.LoginResponse
import com.example.lokanala.data.remote.response_and_request.home.HomeResponse
import com.example.lokanala.data.remote.response_and_request.login.FollowStatusResponse
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantResponse
import com.example.lokanala.data.remote.response_and_request.myumkm.MyUmkmResponse
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.*
import com.example.lokanala.data.remote.response_and_request.notification.NotificationResponse
import com.example.lokanala.data.remote.response_and_request.product.ProductDetailResponse
import com.example.lokanala.data.remote.response_and_request.rating.*

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("user/register")
    suspend fun registerUser(
        @Part("nama") nama: RequestBody,
        @Part("email") email: RequestBody,
        @Part("no_telepon") noTelepon: RequestBody,
        @Part("password") password: RequestBody,
        @Part("fcm_token") fcmToken: RequestBody,
        @Part foto: MultipartBody.Part
    ): Response<LoginResponse>

    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    

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

    @GET("produk/{id_produk}/is-owner/{id_user}")
    fun isProductOwner(
        @Path("id_produk") productId: Int,
        @Path("id_user") userId: Int
    ): Call<OwnerCheckResponse>

    

    @GET("umkm")
    fun getAllUmkm(): Call<HomeResponse>

    @GET("umkm")
    suspend fun getAllUmkmSuspend(): Response<HomeResponse>

    @GET("umkm/{id}")
    suspend fun getUmkmDetail(
        @Path("id") umkmId: Int
    ): Response<UmkmResponse>

    @GET("umkm/my")
    suspend fun getMyUmkmByUserId(
        @Query("id_user") userId: Int
    ): Response<MyUmkmResponse>

    
    @Multipart
    @POST("create-umkm") 
    suspend fun createUmkm(
        @Part("id_user") idUser: RequestBody,
        @Part("id_kategori_umkm") idKategori: RequestBody?,
        @Part("nama_umkm") nama: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("no_telepon") noTelp: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("link_lokasi") linkLokasi: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Response<UmkmSingleResponse>

    @Multipart
    @PUT("update-umkm/{id_umkm}")
    suspend fun updateUmkm(
        @Path("id_umkm") idUmkm: Int,
        @Part("id_kategori_umkm") idKategori: RequestBody?,
        @Part("nama_umkm") nama: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("no_telepon") noTelp: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("link_lokasi") linkLokasi: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Response<UmkmSingleResponse>

    
    @DELETE("delete/umkm/{id_umkm}")
    suspend fun deleteUmkm(
        @Path("id_umkm") idUmkm: Int
    ): Response<UmkmActionResponse>

    @GET("umkm-detail/{id}")
    suspend fun getUmkmById(
        @Path("id") idUmkm: Int
    ): Response<UmkmSingleResponse>

    @GET("umkm-category")
    suspend fun getAllKategoriUmkm(): KategoriUmkmResponse

    @GET("user/my-umkm")
    suspend fun getMyUmkmByToken(
        @Header("Authorization") token: String
    ): MyUmkmMainResponse

    

    @GET("merchant/{id}")
    fun getMerchantDetail(
        @Path("id") id: Long
    ): Call<MerchantResponse>

    @GET("merchant/{id}")
    suspend fun getMerchantDetailSuspend(
        @Path("id") id: Int
    ): MerchantDetailResponse

    

    @GET("produk/{id}")
    fun getProductDetail(
        @Path("id") id: Int
    ): Call<ProductDetailResponse>

    @GET("produk/{id}")
    suspend fun getProductDetailSuspend(
        @Path("id") id: Int
    ): Response<ProductDetailResponse>

    @GET("umkm/{umkmId}/produk")
    suspend fun getProductsByUmkm(
        @Path("umkmId") umkmId: Int
    ): Response<ProductListResponse>

    @DELETE("produk/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<GeneralResponse>

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

    

    @GET("kategori-produk/umkm/{umkmId}")
    suspend fun getCategories(
        @Path("umkmId") umkmId: Int
    ): Response<CategoryResponse>

    @POST("kategori-produk")
    suspend fun addCategory(
        @Body request: CreateCategoryRequest
    ): Response<SingleCategoryResponse>

    @PUT("kategori-produk/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Int,
        @Body request: UpdateCategoryRequest
    ): Response<SingleCategoryResponse>

    @DELETE("kategori-produk/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Int
    ): Response<Unit>

    @PUT("kategori-produk/urutan/{umkmId}")
    suspend fun updateCategoryOrder(
        @Path("umkmId") umkmId: Int,
        @Body request: UpdateCategoryOrderRequest
    ): Response<UpdateCategoryOrderResponse>

    

    @GET("{id_umkm}/promos")
    fun getUmkmPromos(
        @Path("id_umkm") idUmkm: Int
    ): Call<PromoListResponse>

    @GET("promo/{id_promo}")
    fun getPromoDetail(
        @Path("id_promo") idPromo: Int
    ): Call<PromoDetailResponse>

    @POST("{id_umkm}/promos")
    fun createPromo(
        @Path("id_umkm") idUmkm: Int,
        @Body request: CreatePromoRequest
    ): Call<PromoCreateUpdateResponse>

    @PUT("promo/{id_promo}")
    fun updatePromo(
        @Path("id_promo") idPromo: Int,
        @Body request: UpdatePromoRequest
    ): Call<PromoCreateUpdateResponse>

    @DELETE("promo/{id_promo}")
    fun deletePromo(
        @Path("id_promo") idPromo: Int
    ): Call<Unit>

    
    @GET("follow/status/{id_umkm}")
    suspend fun checkFollowStatus(
        @Path("id_umkm") umkmId: Int,
        @Header("Authorization") token: String 
    ): Response<FollowStatusResponse> 

    
    @POST("follow/{id_umkm}")
    suspend fun followUmkm(
        @Path("id_umkm") umkmId: Int,
        @Header("Authorization") token: String
    ): Response<Unit> 

    
    @DELETE("follow/{id_umkm}")
    suspend fun unfollowUmkm(
        @Path("id_umkm") umkmId: Int,
        @Header("Authorization") token: String
    ): Response<Unit> 

    @GET("rating/product/{id_produk}")
    suspend fun getProductRating(
        @Path("id_produk") idProduk: Int
    ): retrofit2.Response<ProductRatingResponse>

    
    @GET("rating/umkm/{id_umkm}")
    suspend fun getUMKMRating(
        @Path("id_umkm") idUmkm: Int
    ): retrofit2.Response<UmkmRatingResponse>

    
    @GET("user/notifications")
    suspend fun getUserNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationResponse>

    
    @GET("user/following")
    suspend fun getUserFollowing(
        @Header("Authorization") token: String
    ): Response<FollowingResponse>
}