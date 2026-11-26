package com.example.gamevault.core.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.authDataStore by preferencesDataStore(name = "gamevault_session_prefs")

data class UserSession(
    val accessToken: String = "",
    val refreshToken: String = "",
    val userId: Int = 0,
    val namaLengkap: String = "",
    val email: String = "",
    val gamerTag: String = "",
    val role: String = "USER"
) {
    val isLoggedIn: Boolean get() = accessToken.isNotBlank() && refreshToken.isNotBlank()
    val isAdmin: Boolean get() = role.equals("ADMIN", ignoreCase = true)
}

class TokenManager(private val context: Context) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_NAMA_LENGKAP = stringPreferencesKey("nama_lengkap")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_GAMER_TAG = stringPreferencesKey("gamer_tag")
        private val KEY_ROLE = stringPreferencesKey("role")
    }

    val sessionFlow: Flow<UserSession> = context.authDataStore.data.map { prefs ->
        UserSession(
            accessToken = prefs[KEY_ACCESS_TOKEN].orEmpty(),
            refreshToken = prefs[KEY_REFRESH_TOKEN].orEmpty(),
            userId = prefs[KEY_USER_ID] ?: 0,
            namaLengkap = prefs[KEY_NAMA_LENGKAP].orEmpty(),
            email = prefs[KEY_EMAIL].orEmpty(),
            gamerTag = prefs[KEY_GAMER_TAG].orEmpty(),
            role = prefs[KEY_ROLE] ?: "USER"
        )
    }

    fun getAccessTokenSync(): String = runBlocking {
        context.authDataStore.data.first()[KEY_ACCESS_TOKEN].orEmpty()
    }

    fun getRefreshTokenSync(): String = runBlocking {
        context.authDataStore.data.first()[KEY_REFRESH_TOKEN].orEmpty()
    }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: Int,
        namaLengkap: String,
        email: String,
        gamerTag: String,
        role: String
    ) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID] = userId
            prefs[KEY_NAMA_LENGKAP] = namaLengkap
            prefs[KEY_EMAIL] = email
            prefs[KEY_GAMER_TAG] = gamerTag
            prefs[KEY_ROLE] = role
        }
    }

    suspend fun updateAccessToken(newAccessToken: String) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = newAccessToken
        }
    }

    suspend fun clearSession() {
        context.authDataStore.edit { it.clear() }
    }
}