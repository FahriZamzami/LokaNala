package com.example.lokanala.ui.screen.promotion_umkm

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.PromoListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

data class Promotion(
    val id: Int,
    var title: String,
    var detail: String,             // deskripsi
    var syarat: String? = null,     // syarat_penggunaan
    var cara: String? = null,       // cara_penggunaan
    var startDate: String,
    var endDate: String
)

class PromotionViewModel : ViewModel() {

    private val TAG = "PromotionViewModel"

    var promotions = mutableStateListOf<Promotion>()
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    /** =====================
     * LOAD PROMO BY UMKM ID
     * ===================== */
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


    /** =====================
     * DELETE PROMO (LOCAL UI)
     * ===================== */
    fun deletePromotion(idPromo: Int) {
        // Hapus lokal dulu
        val removed = promotions.removeAll { it.id == idPromo }
        if (removed) {
            Log.d(TAG, "🗑 Promo dihapus secara lokal: $idPromo")
        } else {
            Log.w(TAG, "⚠ Promo tidak ditemukan di list lokal: $idPromo")
        }

        // Hapus dari server
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = ApiClient.instance.deletePromo(idPromo)
                val response = call.execute()
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Promo dihapus di server: $idPromo")
                } else {
                    Log.e(TAG, "❌ Gagal hapus promo di server: HTTP ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception saat hapus promo di server: ${e.message}", e)
            }
        }
    }

    /** =====================
     * GET SINGLE PROMO
     * ===================== */
    fun getPromotionById(id: Int): Promotion? {
        val promo = promotions.find { it.id == id }
        Log.d(TAG, "🔍 getPromotionById($id) ditemukan: $promo")
        return promo
    }


    /** =====================
     * UPDATE PROMO LOCAL
     * ===================== */
    fun updatePromotion(updated: Promotion) {
        val index = promotions.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            promotions[index] = updated
            Log.d(TAG, "✏️ Promo diupdate lokal: $updated")
        }
    }
}