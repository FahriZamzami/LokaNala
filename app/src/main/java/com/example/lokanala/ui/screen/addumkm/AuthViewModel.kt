package com.example.lokanala.ui.screen.addumkm


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    // Panggil ini saat login berhasil
    fun saveUser(user: UserData, token: String) {
        _currentUser.value = user
        _token.value = token
    }

    fun logout() {
        _currentUser.value = null
        _token.value = null
    }

    // Contoh: load user dari storage (SharedPreferences/DataStore)
    fun loadUser(savedUser: UserData?, savedToken: String?) {
        viewModelScope.launch {
            _currentUser.value = savedUser
            _token.value = savedToken
        }
    }
}
