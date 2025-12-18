package com.example.lokanala.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.example.lokanala.ui.screen.login.LoginViewModel
import com.example.lokanala.ui.screen.myumkm.MyUmkmViewModel
import com.example.lokanala.ui.screen.rating.RatingViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        // 1. MyUmkmViewModel
        if (modelClass.isAssignableFrom(MyUmkmViewModel::class.java)) {
            return MyUmkmViewModel(pref) as T
        }

        // 2. LoginViewModel
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        }

        // 3. RatingViewModel (Butuh SavedStateHandle)
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return RatingViewModel(savedStateHandle, pref) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                // Pastikan import UserPreference berasal dari com.example.lokanala.data.pref
                val pref = UserPreference.getInstance(context.dataStore)
                INSTANCE ?: ViewModelFactory(pref).also { INSTANCE = it }
            }
        }
    }
}