package com.example.lokanala.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.example.lokanala.ui.screen.login.LoginViewModel
import com.example.lokanala.ui.screen.rating.RatingViewModel

class ViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        // 1. UNTUK RATING VIEW MODEL (Butuh SavedStateHandle & UserPreference)
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return RatingViewModel(savedStateHandle, userPreference) as T
        }

        // 2. UNTUK LOGIN VIEW MODEL (Butuh UserPreference untuk simpan sesi)
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userPreference) as T
        }

        // Tambahkan blok 'if' lain di sini jika ada ViewModel lain (misal HomeViewModel)

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                // Mengambil instance UserPreference yang terhubung dengan DataStore
                val pref = UserPreference.getInstance(context.dataStore)

                INSTANCE ?: ViewModelFactory(pref).also { INSTANCE = it }
            }
        }
    }
}