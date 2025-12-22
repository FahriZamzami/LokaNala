package com.example.lokanala.data.pref // Pastikan package ini sesuai folder

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.get
import kotlin.text.get

// --- PENTING: INI DEFINISI DATASTORE ---
// Letakkan di LUAR class, di paling bawah atau paling atas file
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

private val EMAIL_KEY = stringPreferencesKey("email")
private val PHONE_KEY = stringPreferencesKey("phone")
private val PHOTO_KEY = stringPreferencesKey("photo")

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

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            (preferences[TOKEN_KEY] ?: "").isNotEmpty()
        }
    }

    // Fungsi saveSession (Contoh)
    suspend fun saveSession(user: UserProfile) {
        dataStore.edit { preferences ->
            preferences[ID_USER_KEY] = user.idUser.toString()
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[PHONE_KEY] = user.phone
            preferences[PHOTO_KEY] = user.photo ?: ""
            preferences[TOKEN_KEY] = user.token
        }
    }

    // Fungsi logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getUser(): Flow<UserProfile> {
        return dataStore.data.map { preferences ->
            UserProfile(
                idUser = preferences[ID_USER_KEY]?.toInt() ?: -1,
                name = preferences[NAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                phone = preferences[PHONE_KEY] ?: "",
                photo = preferences[PHOTO_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: ""
            )
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

data class UserProfile(
    val idUser: Int,
    val name: String,
    val email: String,
    val phone: String,
    val photo: String?,   // URL foto
    val token: String
)