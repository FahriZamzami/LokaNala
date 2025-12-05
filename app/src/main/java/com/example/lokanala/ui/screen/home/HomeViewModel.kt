package com.example.lokanala.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lokanala.data.remote.response_and_request.home.HomeResponse
import com.example.lokanala.data.remote.retrofit.ApiClient // Gunakan ApiClient
import com.example.lokanala.data.remote.response_and_request.home.Umkm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class HomeUiState(
    val umkmList: List<Umkm> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUmkm()
    }

    private fun loadUmkm() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val client = ApiClient.instance.getAllUmkm()

        client.enqueue(object : Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>
            ) {
                // --- DEBUGGING RESPON SERVER ---
                Log.d("CEK_GAMBAR", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    // Log Jika Sukses
                    Log.d("CEK_GAMBAR", "Sukses! Jumlah Data: ${responseBody?.data?.size}")
                    responseBody?.data?.forEach { item ->
                        Log.d("CEK_GAMBAR", " -- Nama: ${item.namaUmkm}")
                        Log.d("CEK_GAMBAR", " -- Link: ${item.linkLokasi}")
                    }

                    if (responseBody != null && responseBody.success) {
                        val mappedList = responseBody.data.map { item ->
                            Umkm(
                                id = item.idUmkm,
                                name = item.namaUmkm,
                                rating = 4.5,
                                tag = item.kategori?.namaKategori ?: "Umkm",
                                imageUrl = item.linkLokasi,
                                description = item.deskripsi
                            )
                        }
                        _uiState.value = HomeUiState(umkmList = mappedList, isLoading = false)
                    }
                } else {
                    // --- LOG JIKA ERROR DARI SERVER (Misal 404 atau 500) ---
                    Log.e("CEK_GAMBAR", "Gagal Server! Code: ${response.code()}")
                    Log.e("CEK_GAMBAR", "Pesan: ${response.message()}")

                    // Coba baca error body jika ada
                    val errorBody = response.errorBody()?.string()
                    Log.e("CEK_GAMBAR", "Isi Error: $errorBody")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                // --- LOG JIKA KONEKSI PUTUS / FORMAT JSON SALAH ---
                Log.e("CEK_GAMBAR", "KONEKSI GAGAL: ${t.message}")
                t.printStackTrace() // Ini akan mencetak detail error merah di Logcat

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error Koneksi: ${t.message}"
                )
            }
        })
    }
}