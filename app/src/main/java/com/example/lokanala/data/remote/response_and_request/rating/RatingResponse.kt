package com.example.lokanala.data.remote.response_and_request.rating

import com.google.gson.annotations.SerializedName

// ==========================================
// 1. RESPONSE UNTUK GET DATA (List Ulasan)
// ==========================================

data class ReviewListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<ReviewItemApi>
)

data class ReviewItemApi(
    // Sesuai mapping di controller backend (alias id_ulasan)
    @SerializedName("id_rating")
    val idRating: Int,

    @SerializedName("nilai_rating")
    val rating: Int,

    @SerializedName("komentar")
    val komentar: String?,

    @SerializedName("foto")
    val fotoUrl: String?,

    @SerializedName("tanggal_ulasan")
    val tanggal: String?,

    @SerializedName("user")
    val user: UserShort?
)

data class UserShort(
    @SerializedName("id_user")
    val idUser: Int,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("foto_profile")
    val fotoProfile: String?
)

// ==========================================
// 2. RESPONSE UNTUK ADD DATA (Tambah Ulasan)
// ==========================================

data class AddReviewResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: NewReviewData?
)

data class NewReviewData(
    @SerializedName("id_rating")
    val idRating: Int,

    @SerializedName("nilai_rating")
    val rating: Int,

    @SerializedName("komentar")
    val komentar: String?,

    @SerializedName("foto")
    val fotoUrl: String?
)

// ==========================================
// 3. REQUEST BODY UNTUK UPDATE (BARU)
// ==========================================
// Digunakan saat mengirim data edit ke endpoint PUT
data class UpdateReviewRequest(
    @SerializedName("nilai_rating")
    val nilaiRating: Int,

    @SerializedName("komentar")
    val komentar: String
)