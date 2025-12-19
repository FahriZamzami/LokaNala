package com.example.lokanala.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserModel
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.UserProfile
import com.example.lokanala.data.remote.response_and_request.login.LoginRequest
import com.example.lokanala.data.remote.response_and_request.login.UserData
import com.example.lokanala.data.remote.retrofit.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging

// Data State UI
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val success: Boolean = false,
    val user: UserData? = null
)

// Perhatikan: Constructor menerima UserPreference (Butuh Factory)
class LoginViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var fcmToken: String? = null

    fun setFcmToken(token: String) {
        fcmToken = token
    }

    fun fetchFcmTokenAndLogin() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                Log.d("FCM", "FCM token siap: $fcmToken")
            } else {
                Log.e("FCM", "Gagal ambil FCM token", task.exception)
            }

            // Panggil login setelah token siap
            handleLogin()
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    // Tidak perlu parameter Context lagi di sini, karena UserPreference sudah ada di constructor
    fun handleLogin() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(message = "Isi semua data!")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            try {
                val response = ApiClient.instance.loginUser(    LoginRequest(email, password, fcmToken ?: ""))
                val body = response.body()

                if (response.isSuccessful && body != null && body.token != null && body.user != null) {

                    // 1. Simpan Sesi ke DataStore lewat UserPreference
                    val userProfile = UserProfile(
                        idUser = body.user.id_user,
                        name = body.user.nama,
                        email = body.user.email,
                        phone = body.user.no_telepon,
                        photo = body.user.foto_profile,
                        token = body.token
                    )

                    userPreference.saveSession(userProfile)

                    // 2. Update UI
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = true,
                        user = body.user,
                        message = "Login berhasil!"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, success = false, message = body?.message ?: "Login gagal.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, success = false, message = "Error: ${e.localizedMessage}")
            }
        }
    }

    fun clearMessage() { _uiState.value = _uiState.value.copy(message = null) }
}