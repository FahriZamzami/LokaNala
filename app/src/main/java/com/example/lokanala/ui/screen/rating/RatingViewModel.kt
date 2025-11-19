package com.example.lokanala.ui.screen.rating

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lokanala.data.remote.response.rating.AddReviewResponse
import com.example.lokanala.data.remote.response.rating.Review
import com.example.lokanala.data.remote.response.rating.ReviewListResponse
import com.example.lokanala.data.remote.response.rating.UpdateReviewRequest
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

class RatingViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val TAG = "RATING_DEBUG"
    private val productId: Int = savedStateHandle.get<Int>("productId") ?: -1
    private val currentUserId = 1

    private val _reviews = mutableStateListOf<Review>()
    val reviews: List<Review> get() = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        Log.d(TAG, "INIT ViewModel. Product ID: $productId")
        if (productId != -1) {
            loadReviews()
        } else {
            Log.e(TAG, "ERROR: ID Produk Tidak Valid (-1). Cek Navigasi!")
        }
    }

    fun loadReviews() {
        _isLoading.value = true
        val client = ApiClient.instance.getRatingByProduct(productId)

        client.enqueue(object : Callback<ReviewListResponse> {
            override fun onResponse(call: Call<ReviewListResponse>, response: Response<ReviewListResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val body = response.body()
                    val apiData = body?.data ?: emptyList()

                    val uiReviews = apiData.map { apiItem ->
                        val reviewUserId = apiItem.user?.idUser ?: -1
                        Review(
                            id = apiItem.idRating,
                            name = apiItem.user?.nama ?: "User Tanpa Nama",
                            date = formatDateHelper(apiItem.tanggal ?: ""),
                            rating = apiItem.rating,
                            comment = apiItem.komentar ?: "",
                            photoUrl = apiItem.fotoUrl,
                            profilePicUrl = apiItem.user?.fotoProfile,
                            isUserReview = (reviewUserId == currentUserId)
                        )
                    }
                    _reviews.clear()
                    _reviews.addAll(uiReviews)
                } else {
                    Log.e(TAG, "Gagal Load: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ReviewListResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Error Koneksi Load: ${t.message}")
            }
        })
    }

    // --- 1. TAMBAH REVIEW (Fixed List Input) ---
    fun addReview(context: Context, rating: Int, comment: String, photoUris: List<Uri>) {
        _isLoading.value = true

        val idProdukBody = productId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val idUserBody = currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())

        // Mengubah list URI menjadi List<MultipartBody.Part>
        val imagePartsList = photoUris.map { uri ->
            val mimeType = getMimeType(context, uri)
            val extension = getExtension(context, uri)
            val file = uriToFile(uri, context, extension)
            val reqFile = file.asRequestBody((mimeType ?: "image/*").toMediaTypeOrNull())
            val filename = "upload_${System.currentTimeMillis()}.$extension"
            // Penting: Key harus "foto"
            MultipartBody.Part.createFormData("foto", filename, reqFile)
        }

        // PENTING: Jika ApiService.addRating expect List<MultipartBody.Part>, harus dikirim List
        ApiClient.instance.addRating(idProdukBody, idUserBody, ratingBody, commentBody, imagePartsList)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d(TAG, "Sukses Tambah!")
                        loadReviews()
                    } else {
                        Log.e(TAG, "Gagal Tambah: ${response.code()} ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Error Tambah: ${t.message}")
                }
            })
    }

    // --- 2. UPDATE REVIEW (Fixed Multipart List Logic) ---
    fun updateReview(context: Context, reviewId: Int, rating: Int, comment: String, photoUris: List<Uri>) {
        _isLoading.value = true
        Log.d(TAG, "Updating Review ID: $reviewId")

        val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())

        val keptPhotoParts = ArrayList<MultipartBody.Part>()
        val newImageParts = ArrayList<MultipartBody.Part>()

        // 1. Pisahkan dan Proses URI
        photoUris.forEach { uri ->
            if (uri.scheme == "http" || uri.scheme == "https") {
                // FOTO LAMA (URL Server) -> Kirim sebagai text part 'keep_photos'
                val filename = uri.toString().substringAfterLast("/")
                keptPhotoParts.add(
                    MultipartBody.Part.createFormData("keep_photos", filename)
                )
            } else {
                // FOTO BARU (URI Lokal) -> Kirim sebagai file part 'foto'
                val mimeType = getMimeType(context, uri)
                val extension = getExtension(context, uri)
                val file = uriToFile(uri, context, extension)

                val reqFile = file.asRequestBody((mimeType ?: "image/*").toMediaTypeOrNull())
                val filename = "update_${System.currentTimeMillis()}.$extension"
                newImageParts.add(MultipartBody.Part.createFormData("foto", filename, reqFile))
            }
        }

        // Panggil endpoint Multipart
        // Note: ApiService harus diubah untuk menerima List<MultipartBody.Part> untuk 'foto' dan 'keep_photos'
        ApiClient.instance.updateRating(reviewId, ratingBody, commentBody, keptPhotoParts, newImageParts)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        Log.d(TAG, "Sukses Update!")
                        loadReviews()
                    } else {
                        Log.e(TAG, "Gagal Update: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "Error Update: ${t.message}")
                }
            })
    }

    // --- 3. DELETE REVIEW ---
    fun deleteUserReview(reviewId: Int) {
        _isLoading.value = true
        ApiClient.instance.deleteRating(reviewId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _reviews.removeAll { it.id == reviewId }
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    // --- HELPER FUNCTIONS ---

    private fun getExtension(context: Context, uri: Uri): String {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: "jpg"
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: "")).toString()) ?: "jpg"
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }

    private fun uriToFile(uri: Uri, context: Context, extension: String = "jpg"): File {
        val myFile = File.createTempFile("IMG_", ".$extension", context.cacheDir)
        val inputStream = context.contentResolver.openInputStream(uri) as InputStream
        val outputStream = FileOutputStream(myFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return myFile
    }

    private fun formatDateHelper(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(isoDate)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            isoDate.take(10)
        }
    }
}