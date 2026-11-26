package com.example.gamevault.core.network

import com.example.gamevault.data.model.ApiResponse
import com.example.gamevault.data.model.LoginData
import com.example.gamevault.data.model.LoginRequest
import com.example.gamevault.data.model.RefreshRequest
import com.example.gamevault.data.model.RefreshResponseData
import com.example.gamevault.data.model.RegisterRequest
import com.example.gamevault.data.model.UserDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

    @POST("auth/refresh")
    fun refreshTokenSync(@Body request: RefreshRequest): Call<ApiResponse<RefreshResponseData>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>
}
