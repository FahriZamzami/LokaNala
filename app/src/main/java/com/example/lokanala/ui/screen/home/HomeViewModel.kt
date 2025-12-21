package com.example.lokanala.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response_and_request.home.Umkm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val umkmList: List<Umkm> = emptyList(),
    val filteredUmkmList: List<Umkm> = emptyList(),
    val kategoriUmkmList: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class FilterType {
    TIPE_UMKM, TERLARIS, TRENDING, NONE
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
        _selectedFilter.value = FilterType.TIPE_UMKM
        applyFilters()
    }

    /**
     * PERBAIKAN: Menambahkan parameter [latestList] agar fungsi selalu
     * menggunakan data terbaru saat proses asinkron (rating) selesai.
     */
    private fun applyFilters(latestList: List<Umkm>? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            // Gunakan list yang dilempar (jika ada), jika tidak ambil dari state sekarang
            val allUmkm = latestList ?: _uiState.value.umkmList
            var filtered = allUmkm

            // 1. Filter Kategori
            val currentFilter = _selectedFilter.value
            val currentKategori = _selectedKategori.value

            if (currentFilter == FilterType.TIPE_UMKM && currentKategori != null) {
                filtered = filtered.filter { it.tag.equals(currentKategori, ignoreCase = true) }
            }

            // 2. Search
            val query = _searchQuery.value.trim()
            if (query.isNotEmpty()) {
                filtered = filtered.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.tag.contains(query, ignoreCase = true)
                }
            }

            // 3. Sorting (Penting: Rating harus sudah masuk agar TERLARIS bekerja)
            filtered = when (currentFilter) {
                FilterType.TERLARIS -> {
                    filtered.sortedWith(
                        compareByDescending<Umkm> { it.rating }
                            .thenByDescending { it.reviewCount }
                    )
                }
                FilterType.TRENDING -> {
                    filtered.sortedByDescending { it.tanggalTerdaftar ?: "" }
                }
                else -> filtered
            }

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(filteredUmkmList = filtered) }
            }
        }
    }

    private fun loadUmkm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = ApiClient.instance.getAllUmkmSuspend()
                if (response.isSuccessful && response.body()?.success == true) {
                    val responseBody = response.body()!!

                    val mappedList = withContext(Dispatchers.Default) {
                        responseBody.data.map { item ->
                            Umkm(
                                id = item.idUmkm,
                                name = item.namaUmkm,
                                rating = null, // JANGAN set 0.0, set NULL agar UI tahu ini belum dimuat
                                tag = item.kategori?.namaKategori ?: "Umkm",
                                imageUrl = item.gambarUrl,
                                description = item.deskripsi,
                                tanggalTerdaftar = item.tanggalTerdaftar,
                                reviewCount = 0
                            )
                        }
                    }

                    val kategoriList = mappedList.map { it.tag }.distinct().sorted()

                    _uiState.update { it.copy(
                        umkmList = mappedList,
                        kategoriUmkmList = kategoriList,
                        isLoading = false
                    )}

                    // Jalankan applyFilters awal
                    applyFilters(mappedList)

                    // JALANKAN API RATING SECARA ASINKRON
                    mappedList.forEach { umkm ->
                        fetchRatingForUmkm(umkm.id.toLong())
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun fetchRatingForUmkm(umkmId: Long) {
        viewModelScope.launch {
            try {
                // Panggilan langsung (suspend)
                val response = ApiClient.instance.getUMKMRating(umkmId.toInt())

                if (response.isSuccessful) {
                    val ratingBody = response.body()
                    if (ratingBody != null && ratingBody.success) {
                        Log.d("RATING_CHECK", "ID: $umkmId -> Rating: ${ratingBody.rating}")
                        updateUmkmRatingInState(umkmId, ratingBody.rating, ratingBody.totalUlasan)
                    }
                } else {
                    Log.e("API_ERROR", "Gagal fetch rating ID $umkmId: ${response.message()}")
                }
            } catch (e: Exception) {
                // Error "Unable to create call adapter" SEHARUSNYA HILANG setelah tambah 'suspend'
                Log.e("API_EXCEPTION", "ID: $umkmId, Error: ${e.message}")
            }
        }
    }

    private fun updateUmkmRatingInState(umkmId: Long, newRating: Double, newTotalUlasan: Int) {
        var updatedMasterList: List<Umkm> = emptyList()

        _uiState.update { currentState ->
            updatedMasterList = currentState.umkmList.map { umkm ->
                if (umkm.id.toLong() == umkmId) {
                    umkm.copy(rating = newRating, reviewCount = newTotalUlasan)
                } else {
                    umkm
                }
            }
            currentState.copy(umkmList = updatedMasterList)
        }

        // PERBAIKAN: Paksa applyFilters menggunakan list terbaru yang baru saja diupdate
        applyFilters(updatedMasterList)
    }
}