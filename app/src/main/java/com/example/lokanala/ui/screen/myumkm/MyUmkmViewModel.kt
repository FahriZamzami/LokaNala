package com.example.lokanala.ui.screen.myumkm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.api.RetrofitClient
import com.example.lokanala.data.repository.UmkmRepository
import com.example.lokanala.model.MyUmkm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyUmkmUiState(
    val myUmkmList: List<MyUmkm> = emptyList()
)

class MyUmkmViewModel : ViewModel() {

    private val repo = UmkmRepository()
    private val _uiState = MutableStateFlow(MyUmkmUiState())
    val uiState: StateFlow<MyUmkmUiState> = _uiState.asStateFlow()

    fun loadMyUmkm(idUser: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.umkmApi.getMyUmkm(idUser)
                _uiState.value = if (response.isSuccessful && response.body()?.success == true) {
                    MyUmkmUiState(myUmkmList = response.body()!!.data)
                } else {
                    MyUmkmUiState(emptyList())
                }
            } catch (_: Exception) {
                _uiState.value = MyUmkmUiState(emptyList())
            }
        }
    }

    fun deleteUmkm(idUmkm: Int, idUser: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Optimistic update
                val current = _uiState.value.myUmkmList
                _uiState.value = _uiState.value.copy(
                    myUmkmList = current.filterNot { it.id_umkm == idUmkm }
                )

                // Panggil API delete
                repo.deleteUmkm(idUmkm)

                // Reload setelah delete
                loadMyUmkm(idUser)

                onResult(true, "UMKM berhasil dihapus")
            } catch (e: Exception) {
                loadMyUmkm(idUser)
                onResult(false, e.message ?: "Gagal menghapus UMKM")
            }
        }
    }
}
