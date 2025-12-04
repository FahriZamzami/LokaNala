package com.example.lokanala.ui.screen.myumkm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.remote.response.UmkmResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
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
                val userId = user.idUser

                if (userId == -1) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User belum login"
                    )
                    return@launch
                }

                val response = ApiClient.instance.getMyUmkm(userId = userId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        myUmkmList = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan: ${e.message}"
                )
            }
        }
    }
}
