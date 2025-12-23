package com.example.lokanala.ui.screen.edit_promotion_umkm

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.PromoCreateUpdateResponse
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.UpdatePromoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class EditPromoState {
    object Idle : EditPromoState()
    object Loading : EditPromoState()
    data class Success(val message: String) : EditPromoState()
    data class Error(val message: String) : EditPromoState()
}

class EditPromoViewModel : ViewModel() {

    private val api = ApiClient.instance

    private val _state = MutableStateFlow<EditPromoState>(EditPromoState.Idle)
    val state: StateFlow<EditPromoState> = _state

    
    fun updatePromo(
        promoId: Int,
        title: String,
        detail: String? = null,
        terms: String? = null,
        usage: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        _state.value = EditPromoState.Loading

        val request = UpdatePromoRequest(
            nama_promo = title,
            deskripsi = detail,
            syarat_penggunaan = terms,
            cara_penggunaan = usage,
            tanggal_mulai = startDate,
            tanggal_berakhir = endDate
        )

        api.updatePromo(promoId, request).enqueue(object : Callback<PromoCreateUpdateResponse> {
            override fun onResponse(
                call: Call<PromoCreateUpdateResponse>,
                response: Response<PromoCreateUpdateResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _state.value = EditPromoState.Success(body.message)
                    } else {
                        _state.value = EditPromoState.Error(body?.message ?: "Empty body")
                    }
                } else {
                    _state.value = EditPromoState.Error("HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PromoCreateUpdateResponse>, t: Throwable) {
                _state.value = EditPromoState.Error(t.message ?: "Unknown error")
            }
        })
    }

    
    fun deletePromo(promoId: Int) {
        _state.value = EditPromoState.Loading

        api.deletePromo(promoId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    _state.value = EditPromoState.Success("Promo deleted successfully")
                } else {
                    _state.value = EditPromoState.Error("Failed to delete promo: HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _state.value = EditPromoState.Error("Failed to delete promo: ${t.message}")
            }
        })
    }
}