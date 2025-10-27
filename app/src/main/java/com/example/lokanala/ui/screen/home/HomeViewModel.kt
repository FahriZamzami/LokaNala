package com.example.lokanala.ui.screen.home

import androidx.lifecycle.ViewModel
import com.example.lokanala.model.Umkm
import com.example.lokanala.model.dummyUmkmList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val umkmList: List<Umkm> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUmkm()
    }

    private fun loadUmkm() {
        _uiState.value = HomeUiState(umkmList = dummyUmkmList)
    }
}