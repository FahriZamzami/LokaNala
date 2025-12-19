package com.example.lokanala.ui.screen.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokanala.data.pref.UserPreference
import kotlinx.coroutines.launch

data class UserData(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null
)

class ProfileViewModel(
    private val userPreference: UserPreference
) : ViewModel() {

    var user by mutableStateOf(UserData())
        private set

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userPreference.getUser().collect { userData ->

                Log.d("ProfileViewModel", "User photo URL: ${userData.photo}")

                user = UserData(
                    name = userData.name ?: "",
                    email = userData.email ?: "",
                    phone = userData.phone ?: "",
                    photoUrl = userData.photo
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }
}