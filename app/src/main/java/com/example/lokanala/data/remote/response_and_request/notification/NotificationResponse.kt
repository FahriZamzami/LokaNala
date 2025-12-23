package com.example.lokanala.data.remote.response_and_request.notification

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<NotificationApiItem>?
)

data class NotificationApiItem(
    @SerializedName("id_notifikasi") val idNotifikasi: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("isi") val isi: String,
    @SerializedName("tanggal_kirim") val tanggal_kirim: String,
    @SerializedName("dikirim_pada") val dikirim_pada: String,
    @SerializedName("id_user") val id_user: Int
)

