package com.example.lokanala.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response.LoginRequest
import com.example.lokanala.ui.screen.addumkm.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val success: Boolean = false
)

class LoginViewModel(
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun handleLogin() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                message = "Email dan password tidak boleh kosong."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            try {
                val response = ApiClient.instance.loginUser(LoginRequest(email, password))

                if (response.isSuccessful && response.body()?.token != null) {
                    val user = response.body()!!.user!!
                    val token = response.body()!!.token!!

                    // Simpan user & token ke AuthViewModel (shared)
                    authViewModel.saveUser(user, token)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = true,
                        message = "Login berhasil! Selamat datang ${user.nama}"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = false,
                        message = response.body()?.message ?: "Login gagal, periksa email atau password."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    success = false,
                    message = "Terjadi kesalahan: ${e.message}"
                )
            }
        }
    }
}
