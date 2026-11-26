package com.example.gamevault.data.repository

import com.example.gamevault.core.local.TokenManager
import com.example.gamevault.core.local.UserSession
import com.example.gamevault.core.network.AuthApiService
import com.example.gamevault.data.model.LoginRequest
import com.example.gamevault.data.model.RegisterRequest
import com.example.gamevault.data.model.UserDto
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    val sessionFlow: Flow<UserSession> = tokenManager.sessionFlow

    suspend fun register(
        namaLengkap: String,
        email: String,
        gamerTag: String,
        password: String
    ): Result<UserDto> {
        return try {
            val response = authApiService.register(
                RegisterRequest(namaLengkap, email, gamerTag, password)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), "Registrasi gagal.")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Tidak dapat terhubung ke server: ${e.localizedMessage}"))
        }
    }

    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.data != null) {
                val loginData = response.body()!!.data!!
                tokenManager.saveSession(
                    accessToken = loginData.accessToken,
                    refreshToken = loginData.refreshToken,
                    userId = loginData.user.idUser,
                    namaLengkap = loginData.user.namaLengkap,
                    email = loginData.user.email,
                    gamerTag = loginData.user.gamerTag,
                    role = loginData.user.role
                )
                Result.success(loginData.user)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), "Email atau kata sandi salah.")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server: ${e.localizedMessage}"))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            authApiService.logout()
            tokenManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenManager.clearSession()
            Result.success(Unit)
        }
    }

    private fun parseErrorMessage(errorBody: String?, fallback: String): String {
        return try {
            if (errorBody.isNullOrBlank()) fallback
            else JSONObject(errorBody).optString("message", fallback)
        } catch (_: Exception) {
            fallback
        }
    }
}