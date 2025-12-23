package com.example.lokanala.ui.screen.myumkm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response_and_request.ErrorResponse
import com.example.lokanala.data.remote.response_and_request.UmkmResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MyUmkmUiState(
    val isLoading: Boolean = false,
    val myUmkmList: List<UmkmResponse> = emptyList(),
    val errorMessage: String? = null
)

class MyUmkmViewModel(private val pref: UserPreference) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUmkmUiState())
    val uiState: StateFlow<MyUmkmUiState> = _uiState.asStateFlow()

    init {
        loadMyUmkm()
    }

    fun loadMyUmkm() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val user = pref.getSession().first()
                Log.d("MyUmkmViewModel", "Loading UMKM for user ID: ${user.idUser}")

                if (user.idUser == -1) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User belum login")
                    return@launch
                }

                val response = ApiClient.instance.getMyUmkmByUserId(user.idUser)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        
                        val rawJson = Gson().toJson(responseBody)
                        Log.d("CHECK_ID", "RAW JSON DARI SERVER: $rawJson")

                        if (responseBody.success) {
                            val umkmList = responseBody.data ?: emptyList()

                            
                            umkmList.forEachIndexed { index, umkm ->
                                Log.d("CHECK_ID", "Item[$index] -> Nama: ${umkm.nama}, ID di Kotlin: ${umkm.id}")

                                if (umkm.id == 0) {
                                    Log.e("CHECK_ID", "PERINGATAN: ID bernilai 0! Cek @SerializedName di UmkmResponse")
                                }
                            }

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                myUmkmList = umkmList
                            )
                        }
                    }
                    

                } else {
                    val errorBodyString = response.errorBody()?.string() ?: ""
                    Log.e("MyUmkmViewModel", "API Error ${response.code()}: $errorBodyString")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data"
                    )
                }

            } catch (e: Exception) {
                Log.e("MyUmkmViewModel", "Error loading UMKM", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan: ${e.message}"
                )
            }
        }
    }

    fun deleteUmkm(idUmkm: Int) {
        viewModelScope.launch {
            
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val response = ApiClient.instance.deleteUmkm(idUmkm)

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("MyUmkmViewModel", "UMKM ID $idUmkm deleted successfully")

                    
                    
                    val updatedList = _uiState.value.myUmkmList.filter { it.id != idUmkm }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        myUmkmList = updatedList
                    )
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal menghapus UMKM"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                Log.e("MyUmkmViewModel", "Error deleting UMKM", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal menghapus: ${e.message}"
                )
            }
        }
    }
}
