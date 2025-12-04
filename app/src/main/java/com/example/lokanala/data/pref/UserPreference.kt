package com.example.lokanala.data.pref // Pastikan package ini sesuai folder

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// --- PENTING: INI DEFINISI DATASTORE ---
// Letakkan di LUAR class, di paling bawah atau paling atas file
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    // Sesuaikan keys dengan kebutuhan
    private val ID_USER_KEY = stringPreferencesKey("id_user")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val NAME_KEY = stringPreferencesKey("name")

    // Fungsi getSession
    fun getSession(): Flow<UserModel> { // Ganti UserModel dengan model user kamu
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ID_USER_KEY]?.toInt() ?: -1, // Default -1
                preferences[TOKEN_KEY] ?: "",
                preferences[NAME_KEY] ?: ""
            )
        }
    }

    // Fungsi saveSession (Contoh)
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID_USER_KEY] = user.idUser.toString()
            preferences[TOKEN_KEY] = user.token
            preferences[NAME_KEY] = user.name
        }
    }

    // Fungsi logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

// Model Sederhana untuk User (Bisa dipisah file)
data class UserModel(
    val idUser: Int,
    val token: String,
    val name: String
)