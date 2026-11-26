package com.example.gamevault.core.network

import com.example.gamevault.core.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Jangan sisipkan token pada endpoint login/register/refresh
        if (path.endsWith("/auth/login") || path.endsWith("/auth/register") || path.endsWith("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessTokenSync()
        val authenticatedRequest = if (accessToken.isNotBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }
}