package com.example.gamevault.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class RegisterRequest(
    @SerializedName("nama_lengkap") val namaLengkap: String,
    val email: String,
    @SerializedName("gamer_tag") val gamerTag: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class RefreshResponseData(
    @SerializedName("access_token") val accessToken: String
)

data class UserDto(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    val email: String,
    @SerializedName("gamer_tag") val gamerTag: String,
    val role: String
)

data class LoginData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: UserDto
)