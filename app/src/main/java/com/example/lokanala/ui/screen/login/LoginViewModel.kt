package com.example.lokanala.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.remote.response.login.LoginResponse
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.data.remote.response.login.LoginRequest
import com.example.lokanala.data.remote.response.login.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val success: Boolean = false,
    val user: UserData? = null // 👈 TAMBAHAN
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // 👉 FUNCTION UNTUK AMBIL DATA USER DARI SCREEN LAIN
    val currentUser: UserData?
        get() = _uiState.value.user

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun handleLogin() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                message = "Email dan password tidak boleh kosong."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            try {
                val response = ApiClient.instance.loginUser(
                    LoginRequest(email, password)
                )

                val body = response.body()

                if (response.isSuccessful && body?.token != null) {

                    // 👇 SIMPAN USER YANG LOGIN
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = true,
                        user = body.user,    // <- SIMPAN DATA USER DI STATE
                        message = "Login berhasil! Selamat datang ${body.user?.nama.orEmpty()}"
                    )

                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = false,
                        message = body?.message ?: "Login gagal, email atau password salah."
                    )
                }

            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    success = false,
                    message = "Server error: ${e.code()}"
                )

            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    success = false,
                    message = "Tidak ada koneksi internet."
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    success = false,
                    message = "Terjadi kesalahan: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}