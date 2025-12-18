package com.example.lokanala.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.home.HomeResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response.home.Umkm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val umkmList: List<Umkm> = emptyList(),
    val filteredUmkmList: List<Umkm> = emptyList(),
    val kategoriUmkmList: List<String> = emptyList(), // List kategori unik untuk dropdown
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class FilterType {
    TIPE_UMKM,
    TERLARIS,
    TRENDING,
    NONE
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow(FilterType.TRENDING)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()
    
    private val _selectedKategori = MutableStateFlow<String?>(null)
    val selectedKategori: StateFlow<String?> = _selectedKategori.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadUmkm()
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }
    
    fun setFilter(filterType: FilterType) {
        _selectedFilter.value = filterType
        if (filterType != FilterType.TIPE_UMKM) {
            _selectedKategori.value = null
        }
        applyFilters()
    }
    
    fun setKategori(kategori: String?) {
        _selectedKategori.value = kategori
        // Set filter ke Tipe UMKM ketika kategori dipilih atau di-reset
            _selectedFilter.value = FilterType.TIPE_UMKM
        applyFilters()
    }
    
    private fun applyFilters() {
        viewModelScope.launch(Dispatchers.Default) {
            val allUmkm = _uiState.value.umkmList
            var filtered = allUmkm
            
            // 1. Apply filter berdasarkan kategori atau filter type
            when (_selectedFilter.value) {
                FilterType.TIPE_UMKM -> {
                    // Jika kategori dipilih, filter berdasarkan kategori
                    // Jika tidak, tampilkan semua UMKM
                    filtered = _selectedKategori.value?.let { kategori ->
                        allUmkm.filter { umkm ->
                            umkm.tag.equals(kategori, ignoreCase = true)
                        }
                    } ?: allUmkm
                }
                FilterType.TERLARIS -> {
                    // Sort berdasarkan rating tertinggi, kemudian review count
                    filtered = allUmkm.sortedWith(
                        compareByDescending<Umkm> { it.rating }
                            .thenByDescending { it.reviewCount }
                    )
                }
                FilterType.TRENDING -> {
                    // Sort berdasarkan tanggal terdaftar terbaru (UMKM baru lebih trending)
                    filtered = allUmkm.sortedByDescending { umkm ->
                        try {
                            // Parse tanggal terdaftar untuk sorting
                            // Format: "YYYY-MM-DD" atau format lain dari API
                            umkm.tanggalTerdaftar
                        } catch (e: Exception) {
                            "" // Jika parsing gagal, taruh di akhir
                        }
                    }
                }
                FilterType.NONE -> {
                    filtered = allUmkm
                }
            }
            
            // 2. Apply search filter jika ada query
            val query = _searchQuery.value.trim()
            if (query.isNotEmpty()) {
                filtered = filtered.filter { umkm ->
                    umkm.name.contains(query, ignoreCase = true) ||
                    umkm.description?.contains(query, ignoreCase = true) == true ||
                    umkm.tag.contains(query, ignoreCase = true)
                }
            }
            
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(filteredUmkmList = filtered)
            }
        }
    }

    private fun loadUmkm() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = ApiClient.instance.getAllUmkmSuspend()
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    
                    if (responseBody != null && responseBody.success) {
                        val mappedList = withContext(Dispatchers.Default) {
                            responseBody.data.map { item ->
                                Umkm(
                                    id = item.idUmkm,
                                    name = item.namaUmkm,
                                    rating = 4.5, // Default rating, bisa diupdate jika API menyediakan
                                    tag = item.kategori?.namaKategori ?: "Umkm",
                                    imageUrl = item.linkLokasi,
                                    description = item.deskripsi,
                                    tanggalTerdaftar = item.tanggalTerdaftar,
                                    reviewCount = 0 // Default, bisa diisi dari produk jika diperlukan
                                )
                            }
                        }
                        
                        // Ekstrak kategori unik untuk dropdown
                        val kategoriList = withContext(Dispatchers.Default) {
                            responseBody.data
                                .mapNotNull { it.kategori?.namaKategori }
                                .distinct()
                                .sorted()
                        }
                        
                        // Update state dengan data baru
                        _uiState.value = _uiState.value.copy(
                            umkmList = mappedList,
                            filteredUmkmList = mappedList,
                            kategoriUmkmList = kategoriList,
                            isLoading = false
                        )
                        
                        // Apply filter default (Trending) setelah state di-update
                        applyFilters()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Gagal memuat data"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading UMKM: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error Koneksi: ${e.message}"
                )
            }
        }
    }
}