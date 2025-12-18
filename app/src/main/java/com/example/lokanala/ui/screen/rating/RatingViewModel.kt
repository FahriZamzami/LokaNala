package com.example.lokanala.ui.screen.rating

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.rating.AddReviewResponse
import com.example.lokanala.data.remote.response.rating.Review
import com.example.lokanala.data.remote.response.rating.ReviewListResponse
import com.example.lokanala.data.remote.retrofit.ApiClient

// Pastikan import ini sesuai lokasi file Anda
import com.example.lokanala.data.pref.UserPreference

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

// Constructor menerima UserPreference -> Butuh Factory
class RatingViewModel(
    savedStateHandle: SavedStateHandle,
    private val userPreference: UserPreference
) : ViewModel() {

    private val TAG = "RATING_DEBUG"
    private val productId: Int = savedStateHandle.get<Int>("productId") ?: -1

    // Default -1
    private val _currentUserId = MutableStateFlow(-1)
    val currentUserId: StateFlow<Int> = _currentUserId.asStateFlow()

    private val _reviews = mutableStateListOf<Review>()
    val reviews: List<Review> get() = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Otomatis ambil ID dan load data saat ViewModel dibuat
        getRealUserAndLoadData()
    }

    private fun getRealUserAndLoadData() {
        viewModelScope.launch {
            try {
                // 1. Ambil ID User dulu
                val userModel = userPreference.getSession().first()
                _currentUserId.value = userModel.idUser

                Log.d(TAG, "User Login ID: ${_currentUserId.value}")

                // 2. Baru load review setelah ID didapat
                if (productId != -1) {
                    loadReviews()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal ambil sesi user: ${e.message}")
            }
        }
    }

    fun loadReviews() {
        _isLoading.value = true
        ApiClient.instance.getRatingByProduct(productId).enqueue(object : Callback<ReviewListResponse> {
            override fun onResponse(call: Call<ReviewListResponse>, response: Response<ReviewListResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val apiData = response.body()?.data ?: emptyList()
                    val myId = _currentUserId.value

                    val uiReviews = apiData.map { apiItem ->
                        val reviewUserId = apiItem.user?.idUser ?: -1
                        Review(
                            id = apiItem.idRating,
                            userId = reviewUserId,
                            name = apiItem.user?.nama ?: "User",
                            date = formatDateHelper(apiItem.tanggal ?: ""),
                            rating = apiItem.rating,
                            comment = apiItem.komentar ?: "",
                            photoUrl = apiItem.fotoUrl,
                            profilePicUrl = apiItem.user?.fotoProfile,
                            // LOGIKA UTAMA: Cek apakah review ini milik user yang login
                            isUserReview = (reviewUserId == myId)
                        )
                    }
                    _reviews.clear()
                    _reviews.addAll(uiReviews)
                }
            }
            override fun onFailure(call: Call<ReviewListResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    // --- Add, Update, Delete Tetap Sama ---
    fun addReview(context: Context, rating: Int, comment: String, photoUris: List<Uri>) {
        _isLoading.value = true
        val idUserBody = _currentUserId.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val idProdukBody = productId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePartsList = photoUris.map { uri -> prepareFilePart(context, uri, "foto") }

        ApiClient.instance.addRating(idProdukBody, idUserBody, ratingBody, commentBody, imagePartsList)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.success == true) loadReviews()
                }
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) { _isLoading.value = false }
            })
    }

    fun updateReview(context: Context, reviewId: Int, rating: Int, comment: String, photoUris: List<Uri>) {
        _isLoading.value = true
        val idUserBody = _currentUserId.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())
        val keptPhotoParts = ArrayList<MultipartBody.Part>()
        val newImageParts = ArrayList<MultipartBody.Part>()

        photoUris.forEach { uri ->
            if (uri.toString().startsWith("http")) {
                val filename = uri.toString().substringAfterLast("/")
                keptPhotoParts.add(MultipartBody.Part.createFormData("keep_photos", filename))
            } else {
                newImageParts.add(prepareFilePart(context, uri, "foto"))
            }
        }

        ApiClient.instance.updateRating(reviewId, idUserBody, ratingBody, commentBody, keptPhotoParts, newImageParts)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) loadReviews()
                }
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) { _isLoading.value = false }
            })
    }

    fun deleteUserReview(reviewId: Int) {
        _isLoading.value = true
        ApiClient.instance.deleteRating(reviewId, _currentUserId.value).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _isLoading.value = false
                if (response.isSuccessful) _reviews.removeAll { it.id == reviewId }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) { _isLoading.value = false }
        })
    }

    // Helpers
    private fun prepareFilePart(context: Context, uri: Uri, name: String): MultipartBody.Part {
        val extension = getExtension(context, uri)
        val file = uriToFile(uri, context, extension)
        val mime = getMimeType(context, uri) ?: "image/*"
        val reqFile = file.asRequestBody(mime.toMediaTypeOrNull())
        val filename = "img_${System.currentTimeMillis()}.$extension"
        return MultipartBody.Part.createFormData(name, filename, reqFile)
    }
    private fun getExtension(context: Context, uri: Uri): String {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: "jpg" else MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: "")).toString()) ?: "jpg"
    }
    private fun getMimeType(context: Context, uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) context.contentResolver.getType(uri) else MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()))
    }
    private fun uriToFile(uri: Uri, context: Context, extension: String = "jpg"): File {
        val myFile = File.createTempFile("IMG_", ".$extension", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input -> FileOutputStream(myFile).use { output -> input.copyTo(output) } }
        return myFile
    }
    private fun formatDateHelper(isoDate: String): String {
        return try { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(isoDate)!!) } catch (e: Exception) { isoDate.take(10) }
    }
}