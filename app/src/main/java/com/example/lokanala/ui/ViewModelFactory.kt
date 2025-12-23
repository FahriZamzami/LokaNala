package com.example.lokanala.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.example.lokanala.ui.screen.addumkm.AddUmkmViewModel
import com.example.lokanala.ui.screen.editumkm.EditUmkmViewModel
import com.example.lokanala.ui.screen.following.FollowingViewModel
import com.example.lokanala.ui.screen.login.LoginViewModel
import com.example.lokanala.ui.screen.merchant.MerchantViewModel
import com.example.lokanala.ui.screen.profile.ProfileViewModel
import com.example.lokanala.ui.screen.rating.RatingViewModel
import com.example.lokanala.ui.splash.SplashViewModel
import com.example.lokanala.ui.screen.myumkm.MyUmkmViewModel
import com.example.lokanala.ui.screen.notification.NotificationViewModel
import com.example.lokanala.ui.screen.register.RegisterViewModel

class ViewModelFactory(
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {

        return when {

            modelClass.isAssignableFrom(RatingViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                RatingViewModel(savedStateHandle, userPreference) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(MyUmkmViewModel::class.java) -> {
                MyUmkmViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(MerchantViewModel::class.java) -> {
                MerchantViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(AddUmkmViewModel::class.java) -> { 
                AddUmkmViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(EditUmkmViewModel::class.java) -> {
                EditUmkmViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(userPreference) as T
            }

            modelClass.isAssignableFrom(FollowingViewModel::class.java) -> {
                FollowingViewModel(userPreference) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val pref = UserPreference.getInstance(context.dataStore)
                INSTANCE ?: ViewModelFactory(pref).also { INSTANCE = it }
            }
        }
    }
}