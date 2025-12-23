package com.example.lokanala.ui.screen.addumkm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.KategoriUmkmItem
import com.example.lokanala.data.remote.response_and_request.UmkmRequest
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class AddUmkmState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<KategoriUmkmItem> = emptyList()
)

class AddUmkmViewModel(private val pref: UserPreference) : ViewModel() {
    private val _state = MutableStateFlow(AddUmkmState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getAllKategoriUmkm()
                if (response.success) {
                    _state.update { it.copy(categories = response.data ?: emptyList()) }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun saveUmkm(
        idKategori: Int?,
        nama: String,
        alamat: String,
        noTelp: String,
        deskripsi: String,
        linkLokasi: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _state.value = AddUmkmState(isLoading = true)
            try {
                val user = pref.getSession().first()
                val textType = "text/plain".toMediaTypeOrNull()

                
                val idUserBody = user.idUser.toString().toRequestBody(textType)
                val namaBody = nama.toRequestBody(textType)
                val alamatBody = alamat.toRequestBody(textType)
                val noTelpBody = noTelp.toRequestBody(textType)
                val deskripsiBody = deskripsi.toRequestBody(textType)
                val linkBody = linkLokasi.toRequestBody(textType)
                val idKategoriBody = idKategori?.toString()?.toRequestBody(textType)

                val gambarPart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    
                    MultipartBody.Part.createFormData("gambar", it.name, requestFile)
                }

                
                val response = ApiClient.instance.createUmkm(
                    idUserBody,
                    idKategoriBody,
                    namaBody,
                    alamatBody,
                    noTelpBody,
                    deskripsiBody,
                    linkBody,
                    gambarPart
                )

                if (response.isSuccessful) {
                    _state.value = AddUmkmState(isSuccess = true, isLoading = false)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _state.value = AddUmkmState(errorMessage = "Gagal: $errorMsg", isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("ADD_UMKM_ERROR", "Detail: ${e.stackTraceToString()}")
                
                _state.value = AddUmkmState(
                    isLoading = false,
                    errorMessage = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}