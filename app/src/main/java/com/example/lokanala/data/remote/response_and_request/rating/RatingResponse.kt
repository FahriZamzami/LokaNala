package com.example.lokanala.data.remote.response_and_request.rating

import com.google.gson.annotations.SerializedName

data class ReviewListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<ReviewItemApi>
)

data class ReviewItemApi(
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

data class UpdateReviewRequest(
    @SerializedName("nilai_rating")
    val nilaiRating: Int,

    @SerializedName("komentar")
    val komentar: String
)

data class ProductRatingResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("rating")
    val rating: Double
)

data class ProductRatingData(
    @SerializedName("rating")
    val rating: Double
)

data class UmkmRatingResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("total_ulasan")
    val totalUlasan: Int
)

data class OwnerCheckResponse(
    val success: Boolean,
    val isOwner: Boolean
)