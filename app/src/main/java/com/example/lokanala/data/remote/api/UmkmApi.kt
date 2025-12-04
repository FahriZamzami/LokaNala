package com.example.lokanala.data.remote.api

import com.example.lokanala.data.remote.response.MyUmkmResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface UmkmApi {

    @Multipart
    @POST("umkm/add")
    suspend fun addUMKM(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part gambar: MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("umkm/user/{id_user}")
    suspend fun getMyUmkm(
        @Path("id_user") idUser: Int
    ): Response<MyUmkmResponse>

    @DELETE("umkm/{id}")
    suspend fun deleteUmkm(
        @Path("id") id: Int
    ): Response<ResponseBody>
}
