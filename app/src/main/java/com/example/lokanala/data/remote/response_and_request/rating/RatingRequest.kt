package com.example.lokanala.data.remote.response_and_request.rating

import android.net.Uri

//RatingRequest
data class Review(
    val id: Int = 0, // Default 0 agar aman saat inisialisasi data baru
    val userId: Int = 0,
    val name: String = "",
    val date: String = "",
    val rating: Int = 0,
    val comment: String = "",

    // URL dari Internet (Backend)
    val photoUrl: String? = null,

    val profilePicUrl: String? = null,
    val isUserReview: Boolean = false,

    // --- TAMBAHKAN INI ---
    // List URI Lokal (Dari Galeri/Kamera HP)
    // Diperlukan oleh AddEditReviewSheetContent
    val photoUris: List<Uri> = emptyList()
) {
    // Helper property untuk UI
    val allPhotoUrls: List<String>
        get() = photoUrl?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    val hasPhoto: Boolean
        get() = allPhotoUrls.isNotEmpty() || photoUris.isNotEmpty()
}