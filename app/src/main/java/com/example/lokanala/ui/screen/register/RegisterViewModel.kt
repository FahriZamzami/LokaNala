package com.example.lokanala.ui.screen.register

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.UserProfile
import com.example.lokanala.data.remote.retrofit.ApiClient
import com.example.lokanala.ui.screen.addumkm.uriToFile
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class RegisterUiState(
    val nama: String = "",
    val email: String = "",
    val noTelp: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNamaChange(newValue: String) { _uiState.value = _uiState.value.copy(nama = newValue) }
    fun onEmailChange(newValue: String) { _uiState.value = _uiState.value.copy(email = newValue) }
    fun onNoTelpChange(newValue: String) { _uiState.value = _uiState.value.copy(noTelp = newValue) }
    fun onPasswordChange(newValue: String) { _uiState.value = _uiState.value.copy(password = newValue) }

    fun fetchFcmAndRegister(context: Context, imageUri: Uri) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val fcmToken = if (task.isSuccessful) task.result else ""
            register(context, imageUri, fcmToken)
        }
    }

    private fun register(context: Context, imageUri: Uri, fcmToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val file = withContext(Dispatchers.IO) { uriToFile(context, imageUri) }

                if (file != null) {

                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                    val fotoPart = MultipartBody.Part.createFormData("foto_profile", file.name, requestImageFile)

                    val namaPart = _uiState.value.nama.toRequestBody("text/plain".toMediaType())
                    val emailPart = _uiState.value.email.toRequestBody("text/plain".toMediaType())
                    val telpPart = _uiState.value.noTelp.toRequestBody("text/plain".toMediaType())
                    val passPart = _uiState.value.password.toRequestBody("text/plain".toMediaType())
                    val fcmPart = fcmToken.toRequestBody("text/plain".toMediaType())

                    val response = ApiClient.instance.registerUser(
                        namaPart, emailPart, telpPart, passPart, fcmPart, fotoPart
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success) {
                            body.user?.let { userFromServer ->
                                val profile = UserProfile(
                                    idUser = userFromServer.id_user,
                                    name = userFromServer.nama,
                                    email = userFromServer.email,
                                    phone = userFromServer.no_telepon,
                                    photo = userFromServer.foto_profile,
                                    token = body.token ?: ""
                                )
                                userPreference.saveSession(profile)
                                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = body?.message)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal: $errorBody")
                    }
                } else {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memproses gambar. Pastikan file ada."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}