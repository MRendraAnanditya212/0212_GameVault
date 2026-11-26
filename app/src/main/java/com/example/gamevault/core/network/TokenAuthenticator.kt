package com.example.gamevault.core.network

import com.example.gamevault.core.local.TokenManager
import com.example.gamevault.data.model.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApiService: AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Cegah loop tak terbatas jika retry tetap 401
        if (responseCount(response) >= 2) {
            runBlocking { tokenManager.clearSession() }
            return null
        }

        val currentRefreshToken = tokenManager.getRefreshTokenSync()
        if (currentRefreshToken.isBlank()) {
            runBlocking { tokenManager.clearSession() }
            return null
        }

        synchronized(this) {
            val latestAccessToken = tokenManager.getAccessTokenSync()
            val requestAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // Jika thread lain sudah memperbarui token, ulangi request dengan token terbaru
            if (latestAccessToken.isNotBlank() && latestAccessToken != requestAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestAccessToken")
                    .build()
            }

            return try {
                val refreshCall = refreshApiService.refreshTokenSync(RefreshRequest(currentRefreshToken))
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful && refreshResponse.body()?.data != null) {
                    val newToken = refreshResponse.body()!!.data!!.accessToken
                    runBlocking { tokenManager.updateAccessToken(newToken) }

                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    runBlocking { tokenManager.clearSession() }
                    null
                }
            } catch (e: Exception) {
                runBlocking { tokenManager.clearSession() }
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}