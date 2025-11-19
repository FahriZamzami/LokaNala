package com.example.lokanala.ui.screen.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lokanala.data.remote.response.product.ProductDetailData
import com.example.lokanala.data.remote.response.product.ProductDetailResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

// State untuk UI Detail
data class DetailUiState(
    val isLoading: Boolean = false,
    val product: ProductDetailData? = null,
    val errorMessage: String? = null
)

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        // PERBAIKAN:
        // Langsung ambil sebagai Int karena di NavGraph sudah didefinisikan type = NavType.IntType.
        // Jangan mencoba mengambil sebagai String agar tidak terjadi ClassCastException.
        val productId = savedStateHandle.get<Int>("productId") ?: -1

        Log.d("DetailViewModel", "Product ID diterima: $productId")

        if (productId != -1) {
            loadProductDetail(productId)
        } else {
            _uiState.value = DetailUiState(errorMessage = "ID Produk tidak valid/tidak ditemukan")
        }
    }

    private fun loadProductDetail(id: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Pastikan ApiClient.instance tidak error saat inisialisasi
        val client = ApiClient.instance.getProductDetail(id)

        client.enqueue(object : Callback<ProductDetailResponse> {
            override fun onResponse(
                call: Call<ProductDetailResponse>,
                response: Response<ProductDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _uiState.value = DetailUiState(
                            isLoading = false,
                            product = body.data
                        )
                    } else {
                        _uiState.value = DetailUiState(
                            isLoading = false,
                            errorMessage = body?.message ?: "Data kosong"
                        )
                    }
                } else {
                    _uiState.value = DetailUiState(
                        isLoading = false,
                        errorMessage = "Gagal memuat: ${response.message()} (${response.code()})"
                    )
                }
            }

            override fun onFailure(call: Call<ProductDetailResponse>, t: Throwable) {
                Log.e("DetailViewModel", "Error: ${t.message}")
                _uiState.value = DetailUiState(
                    isLoading = false,
                    errorMessage = "Koneksi error: ${t.message}"
                )
            }
        })
    }

    fun formatRupiah(number: Double): String {
        return try {
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            formatRupiah.format(number).replace("Rp", "Rp ").substringBeforeLast(",00")
        } catch (e: Exception) {
            "Rp $number"
        }
    }
}