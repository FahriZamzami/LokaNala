package com.example.lokanala.ui.screen.merchant

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantData
import com.example.lokanala.data.remote.response_and_request.merchant.MerchantResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

// State untuk UI
data class MerchantUiState(
    val isLoading: Boolean = false,
    val merchantData: MerchantData? = null,
    val errorMessage: String? = null
)

class MerchantViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MerchantUiState())
    val uiState: StateFlow<MerchantUiState> = _uiState.asStateFlow()

    // Fungsi dipanggil saat layar dibuka
    fun loadMerchantDetail(id: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val client = ApiClient.instance.getMerchantDetail(id)
        client.enqueue(object : Callback<MerchantResponse> {
            override fun onResponse(
                call: Call<MerchantResponse>,
                response: Response<MerchantResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _uiState.value = MerchantUiState(
                            isLoading = false,
                            merchantData = body.data
                        )
                    } else {
                        _uiState.value = MerchantUiState(
                            isLoading = false,
                            errorMessage = body?.message ?: "Data kosong"
                        )
                    }
                } else {
                    _uiState.value = MerchantUiState(
                        isLoading = false,
                        errorMessage = "Gagal memuat: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<MerchantResponse>, t: Throwable) {
                Log.e("MerchantVM", "Error: ${t.message}")
                _uiState.value = MerchantUiState(
                    isLoading = false,
                    errorMessage = "Koneksi error: ${t.message}"
                )
            }
        })
    }

    // Helper untuk format Rupiah
    fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ").substringBeforeLast(",00")
    }
}