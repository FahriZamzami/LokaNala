package com.example.lokanala.data.remote.response_and_request.rating

import android.net.Uri

data class Review(
    val id: Int = 0, 
    val userId: Int = 0,
    val name: String = "",
    val date: String = "",
    val rating: Int = 0,
    val comment: String = "",

    val photoUrl: String? = null,

    val profilePicUrl: String? = null,
    val isUserReview: Boolean = false,

    val photoUris: List<Uri> = emptyList()
) {
    
    val allPhotoUrls: List<String>
        get() = photoUrl?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    val hasPhoto: Boolean
        get() = allPhotoUrls.isNotEmpty() || photoUris.isNotEmpty()
}