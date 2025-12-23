package com.example.lokanala.ui.screen.promo

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.PromoListResponse
import com.example.lokanala.ui.screen.promotion_umkm.Promotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PromoViewModel : ViewModel() {

    private val TAG = "PromoViewModel"

    var promotions = mutableStateListOf<Promotion>()
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    
    fun loadPromotionsForUmkm(umkmId: Int) {
        Log.d(TAG, "🔵 loadPromotionsForUmkm dipanggil dengan UMKM ID = $umkmId")

        viewModelScope.launch {
            loading = true
            errorMessage = null

            val result = withContext(Dispatchers.IO) {
                try {
                    val call = ApiClient.instance.getUmkmPromos(umkmId)

                    Log.d(TAG, "🌐 Request URL: ${call.request().url}")
                    Log.d(TAG, "📨 Mengirim request ke server...")

                    call.execute()
                } catch (e: Exception) {
                    Log.e(TAG, "❌ ERROR saat request: ${e.message}")
                    e
                }
            }

            when (result) {
                is Exception -> {
                    errorMessage = result.localizedMessage
                    Log.e(TAG, "❌ Exception terjadi: $errorMessage")
                }

                is Response<*> -> {
                    Log.d(TAG, "📥 Response code: ${result.code()}")

                    if (result.isSuccessful) {
                        val body = result.body() as PromoListResponse
                        Log.d(TAG, "✅ Response sukses. Jumlah data promo: ${body.data.size}")

                        promotions.clear()
                        promotions.addAll(
                            body.data.map {
                                Log.d(TAG, "📌 Promo diterima dari DB: id=${it.id_promo}, nama=${it.nama_promo}")

                                Promotion(
                                    id = it.id_promo,
                                    title = it.nama_promo ?: "",
                                    detail = it.deskripsi ?: "",
                                    syarat = it.syarat_penggunaan ?: "",
                                    cara = it.cara_penggunaan ?: "",
                                    startDate = it.tanggal_mulai ?: "",
                                    endDate = it.tanggal_berakhir ?: ""
                                )
                            }
                        )
                    } else {
                        errorMessage = "Gagal memuat promo: ${result.message()}"
                        Log.e(TAG, "❗ Response gagal: ${result.code()} - ${result.message()}")
                    }
                }
            }

            loading = false
        }
    }

}