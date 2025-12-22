package com.example.lokanala.ui.screen.add_promotion_umkm

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.CreatePromoRequest
import com.example.lokanala.data.remote.response_and_request.myumkmpromo.PromoCreateUpdateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class AddPromoState {
    object Idle : AddPromoState()
    object Loading : AddPromoState()
    data class Success(val message: String) : AddPromoState()
    data class Error(val message: String) : AddPromoState()
}

class AddPromoViewModel : ViewModel() {

    private val api = ApiClient.instance

    private val _state = MutableStateFlow<AddPromoState>(AddPromoState.Idle)
    val state: StateFlow<AddPromoState> = _state

    fun addPromo(
        umkmId: Int,
        title: String,
        description: String? = null,
        terms: String? = null,
        usage: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        _state.value = AddPromoState.Loading

        // Buat request JSON
        val request = CreatePromoRequest(
            nama_promo = title,
            deskripsi = description,
            syarat_penggunaan = terms,
            cara_penggunaan = usage,
            tanggal_mulai = startDate,
            tanggal_berakhir = endDate
        )

        api.createPromo(umkmId, request)
            .enqueue(object : Callback<PromoCreateUpdateResponse> {
                override fun onResponse(
                    call: Call<PromoCreateUpdateResponse>,
                    response: Response<PromoCreateUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success) {
                            Log.d("AddPromo", "Success: ${body.message}")
                            _state.value = AddPromoState.Success(body.message)
                        } else {
                            val msg = body?.message ?: "Empty body"
                            Log.e("AddPromo", "Backend error: $msg")
                            _state.value = AddPromoState.Error("Backend error: $msg")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AddPromo", "HTTP ${response.code()}: $errorBody")
                        _state.value = AddPromoState.Error("HTTP ${response.code()}: $errorBody")
                    }
                }

                override fun onFailure(call: Call<PromoCreateUpdateResponse>, t: Throwable) {
                    Log.e("AddPromo", "Exception: ${t.message}", t)
                    _state.value = AddPromoState.Error("Exception: ${t.message}")
                }
            })
    }
}