package com.example.lokanala.ui.screen.rating

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

// Model Review sekarang mendukung banyak foto
data class Review(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: String,
    val rating: Int,
    val comment: String,
    val photoUris: List<Uri> = emptyList()
) {
    val hasPhoto: Boolean
        get() = photoUris.isNotEmpty()
}

class RatingViewModel : ViewModel() {

    // Data dummy seperti versi lama
    var reviews = mutableStateListOf(
        Review(
            id = "1",
            name = "Ratna Solihin",
            date = "10 Oktober 2025",
            rating = 5,
            comment = "Porsi besar dan pelayanan cepat. Lorem ipsum dolor sit amet.",
            photoUris = emptyList()
        ),
        Review(
            id = "2",
            name = "Ahmad Amaik",
            date = "9 Oktober 2025",
            rating = 4,
            comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            photoUris = emptyList()
        ),
        Review(
            id = "3",
            name = "Budi Santoso",
            date = "7 Oktober 2025",
            rating = 5,
            comment = "Pelayanan ramah dan cepat.",
            photoUris = emptyList()
        ),
        Review(
            id = "4",
            name = "Siti Rahma",
            date = "6 Oktober 2025",
            rating = 4,
            comment = "Tempat nyaman, makanan enak.",
            photoUris = emptyList()
        ),
        Review(
            id = "5",
            name = "Fajar Hidayat",
            date = "5 Oktober 2025",
            rating = 5,
            comment = "Pelayanan ramah dan cepat.",
            photoUris = emptyList()
        ),
        Review(
            id = "6",
            name = "Anisa Putri",
            date = "4 Oktober 2025",
            rating = 3,
            comment = "Makanan enak tapi penyajiannya agak lama.",
            photoUris = emptyList()
        ),
        Review(
            id = "7",
            name = "Dwi Kurniawan",
            date = "3 Oktober 2025",
            rating = 2,
            comment = "Kurang sesuai ekspektasi.",
            photoUris = emptyList()
        )
    )
        private set

    var userReview = mutableStateOf<Review?>(null)
        private set

    fun addReview(rating: Int, comment: String, photoUris: List<Uri>) {
        if (rating <= 0 || comment.isBlank()) return

        val newReview = Review(
            id = UUID.randomUUID().toString(),
            name = "Anda",
            date = currentDate(),
            rating = rating,
            comment = comment,
            photoUris = photoUris
        )

        userReview.value?.let { reviews.remove(it) }

        reviews.add(0, newReview)
        userReview.value = newReview
    }

    fun editUserReview(newRating: Int, newComment: String, photoUris: List<Uri>) {
        val current = userReview.value ?: return
        val index = reviews.indexOfFirst { it.id == current.id }
        if (index < 0) return

        val updated = current.copy(
            rating = newRating,
            comment = newComment,
            photoUris = photoUris,
            date = currentDate()
        )

        reviews[index] = updated
        userReview.value = updated
    }

    fun deleteUserReview() {
        userReview.value?.let {
            reviews.removeAll { r -> r.id == it.id }
            userReview.value = null
        }
    }

    private fun currentDate(): String =
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
}