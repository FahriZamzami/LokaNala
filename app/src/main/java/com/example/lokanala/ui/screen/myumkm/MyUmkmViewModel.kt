package com.example.lokanala.ui.screen.myumkm

import androidx.lifecycle.ViewModel
import com.example.lokanala.model.MyUmkm
import com.example.lokanala.model.dummyMyUmkmList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MyUmkmUiState(
    val myUmkmList: List<MyUmkm> = emptyList()
)

class MyUmkmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyUmkmUiState())
    val uiState: StateFlow<MyUmkmUiState> = _uiState.asStateFlow()

    init {
        loadMyUmkm()
    }

    private fun loadMyUmkm() {
        _uiState.value = MyUmkmUiState(myUmkmList = dummyMyUmkmList)
    }
}