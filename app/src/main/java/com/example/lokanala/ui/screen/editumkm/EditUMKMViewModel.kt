package com.example.lokanala.ui.screen.editumkm

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.KategoriUmkmItem
import com.example.lokanala.data.remote.response_and_request.UmkmResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.ui.screen.addumkm.AddUmkmState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EditUmkmViewModel(private val pref: UserPreference) : ViewModel() {

    private val _state = MutableStateFlow(AddUmkmState())
    val state = _state.asStateFlow()

    private val _categories = MutableStateFlow<List<KategoriUmkmItem>>(emptyList())
    val categories = _categories.asStateFlow()

    
    private val _umkmDetail = MutableStateFlow<UmkmResponse?>(null)
    val umkmDetail = _umkmDetail.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Mengambil daftar kategori dari database untuk dropdown
     */
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = ApiClient.instance.getAllKategoriUmkm()
                if (response.success) {
                    _categories.value = response.data ?: emptyList()
                    Log.d("EditUmkmVM", "Kategori berhasil dimuat: ${_categories.value.size} item")
                }
            } catch (e: Exception) {
                Log.e("EditUmkmVM", "Gagal muat kategori: ${e.message}")
            }
        }
    }

    /**
     * MENGAMBIL DATA DETAIL TERBARU DARI SERVER
     * Digunakan untuk mengisi (pre-fill) form edit dengan data paling akurat
     */
    fun fetchUmkmDetail(idUmkm: Int) {

        if (idUmkm <= 0) {
            Log.e("EditUmkmVM", "Error: ID yang diterima tidak valid (0)")
            _state.update { it.copy(errorMessage = "ID UMKM tidak valid") }
            return
        }

        viewModelScope.launch {

            Log.d("EDIT_DEBUG", "ID UMKM yang diterima dari Navigasi: $idUmkm")

            _state.update { it.copy(isLoading = true) }
            try {
                Log.d("EditUmkmVM", "Fetching detail untuk ID: $idUmkm")
                val response = ApiClient.instance.getUmkmById(idUmkm)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    _umkmDetail.value = data
                    _state.update { it.copy(isLoading = false) }
                    Log.d("EditUmkmVM", "Data Detail Berhasil Diterima: ${data?.nama}")
                } else {
                    val errorMsg = response.message()
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal mengambil detail: $errorMsg") }
                }
            } catch (e: Exception) {
                Log.e("EditUmkmVM", "Fetch Detail Error: ${e.message}")
                _state.update { it.copy(isLoading = false, errorMessage = "Koneksi gagal: ${e.message}") }
            }
        }
    }

    /**
     * Fungsi untuk memperbarui data UMKM (Update)
     */
    fun updateUmkm(
        idUmkm: Int,
        idKategori: Int?,
        nama: String,
        alamat: String,
        noTelp: String,
        deskripsi: String,
        linkLokasi: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val textType = "text/plain".toMediaTypeOrNull()

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

                Log.d("EditUmkmVM", "Mengirim update ke server untuk ID: $idUmkm")
                val response = ApiClient.instance.updateUmkm(
                    idUmkm = idUmkm,
                    idKategori = idKategoriBody,
                    nama = namaBody,
                    alamat = alamatBody,
                    noTelp = noTelpBody,
                    deskripsi = deskripsiBody,
                    linkLokasi = linkBody,
                    gambar = gambarPart
                )

                if (response.isSuccessful) {
                    _state.update { it.copy(isSuccess = true, isLoading = false) }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal Update: $errorMsg") }
                }
            } catch (e: Exception) {
                Log.e("EditUmkmVM", "Update Exception: ${e.message}")
                _state.update { it.copy(isLoading = false, errorMessage = "Koneksi bermasalah: ${e.localizedMessage}") }
            }
        }
    }

    /**
     * Fungsi untuk menghapus UMKM (Delete)
     */
    fun deleteUmkm(idUmkm: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = ApiClient.instance.deleteUmkm(idUmkm)
                if (response.isSuccessful) {
                    _state.update { it.copy(isSuccess = true, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal Hapus") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Terjadi kesalahan") }
            }
        }
    }

    fun resetState() {
        _state.value = AddUmkmState()
    }
}