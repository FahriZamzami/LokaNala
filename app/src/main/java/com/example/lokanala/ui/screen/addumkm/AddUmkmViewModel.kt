package com.example.lokanala.ui.screen.addumkm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.KategoriUMKM
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.repository.UmkmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddUmkmViewModel : ViewModel() {

    private val repo = UmkmRepository()

    // ================================
    // STATE KATEGORI
    // ================================
    private val _kategoriList = MutableStateFlow<List<KategoriUMKM>>(emptyList())
    val kategoriList: StateFlow<List<KategoriUMKM>> get() = _kategoriList

    private val _loadingKategori = MutableStateFlow(false)
    val loadingKategori: StateFlow<Boolean> get() = _loadingKategori

    private val _errorKategori = MutableStateFlow<String?>(null)
    val errorKategori: StateFlow<String?> get() = _errorKategori


    fun loadKategori() {
        viewModelScope.launch {
            try {
                _loadingKategori.value = true

                val response = ApiClient.instance.getKategori()

                if (response.success) {
                    _kategoriList.value = response.data
                } else {
                    _errorKategori.value = "Gagal memuat kategori"
                }

            } catch (e: Exception) {
                _errorKategori.value = e.localizedMessage
            } finally {
                _loadingKategori.value = false
            }
        }
    }


    // ================================
    // ADD UMKM — gunakan Repository (Multipart)
    // ================================
    fun addUMKM(
        idUser: Int,
        idKategori: Int,
        nama: String,
        alamat: String,
        noTelp: String,
        deskripsi: String,
        linkLokasi: String,
        imageUri: Uri?,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repo.addUMKM(
                    idUser = idUser,
                    idKategori = idKategori,
                    nama = nama,
                    alamat = alamat,
                    noTelp = noTelp,
                    deskripsi = deskripsi,
                    linkLokasi = linkLokasi,
                    imageUri = imageUri
                )

                callback(true, result)

            } catch (e: Exception) {
                callback(false, e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
