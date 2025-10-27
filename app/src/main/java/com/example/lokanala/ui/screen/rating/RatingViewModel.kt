package com.example.lokanala.ui.screen.rating

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

data class Review(
    val id: String = UUID.randomUUID().toString(), // 🔹 id unik untuk tiap review
    val name: String,
    val date: String,
    val rating: Int,
    val comment: String,
    val hasPhoto: Boolean = false
)

class RatingViewModel : ViewModel() {

    // daftar semua ulasan (mutable agar bisa diubah langsung)
    var reviews = mutableStateListOf(
        Review("1", "Ratna Solihin", "10 Oktober 2025", 5, "Porsi besar dan pelayanan cepat. Lorem ipsum dolor sit amet.", true),
        Review("2", "Ahmad Amaik", "9 Oktober 2025", 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
        Review("3", "Budi Santoso", "7 Oktober 2025", 5, "Pelayanan ramah dan cepat.", true),
        Review("4", "Siti Rahma", "6 Oktober 2025", 4, "Tempat nyaman, makanan enak."),
        Review("5", "Fajar Hidayat", "5 Oktober 2025", 5, "Pelayanan ramah dan cepat."),
        Review("6", "Anisa Putri", "4 Oktober 2025", 3, "Makanan enak tapi penyajiannya agak lama."),
        Review("7", "Dwi Kurniawan", "3 Oktober 2025", 2, "Kurang sesuai ekspektasi.")
    )
        private set

    // review milik user (jika sudah pernah mengisi)
    var userReview = mutableStateOf<Review?>(null)
        private set

    // tambah review baru
    fun addReview(rating: Int, comment: String, hasPhoto: Boolean) {
        if (rating <= 0 || comment.isBlank()) return

        val newReview = Review(
            id = UUID.randomUUID().toString(), // 🔹 id baru untuk review user
            name = "Anda",
            date = currentDate(),
            rating = rating,
            comment = comment,
            hasPhoto = hasPhoto
        )

        // hapus review lama user jika ada
        userReview.value?.let { reviews.remove(it) }

        reviews.add(0, newReview)
        userReview.value = newReview
    }

    // edit review user
    fun editUserReview(newRating: Int, newComment: String, hasPhoto: Boolean) {
        val current = userReview.value ?: return
        val index = reviews.indexOfFirst { it.id == current.id }
        if (index < 0) return

        val updated = current.copy(
            rating = newRating,
            comment = newComment,
            hasPhoto = hasPhoto,
            date = currentDate()
        )

        reviews[index] = updated
        userReview.value = updated
    }

    // hapus review user
    fun deleteUserReview() {
        userReview.value?.let {
            reviews.removeAll { r -> r.id == it.id }
            userReview.value = null
        }
    }

    // ambil tanggal hari ini (format Indonesia)
    private fun currentDate(): String =
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
}
