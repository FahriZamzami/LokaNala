package com.example.lokanala.ui.screen.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// State untuk input fields
data class LoginUiState(
    val email: String = "",
    val password: String = ""
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    // Nanti bisa ditambahkan fungsi untuk handle login
    fun handleLogin() {
        // TODO: Tambahkan logika autentikasi di sini
        println("Login attempt: Email=${_uiState.value.email}, Pass=${_uiState.value.password}")
    }
}