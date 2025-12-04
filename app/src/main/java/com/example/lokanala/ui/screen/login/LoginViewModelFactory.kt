package com.example.lokanala.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lokanala.ui.screen.addumkm.AuthViewModel

class LoginViewModelFactory(
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
