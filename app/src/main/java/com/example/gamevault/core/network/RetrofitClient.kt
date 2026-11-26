package com.example.gamevault.core.network

import android.content.Context
import com.example.gamevault.core.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val RAWG_BASE_URL = "https://api.rawg.io/api/"
    private const val BACKEND_BASE_URL = "http://10.0.2.2:5000/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val rawgOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val rawgApiService: RawgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RAWG_BASE_URL)
            .client(rawgOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RawgApiService::class.java)
    }

    @Volatile
    private var backendRetrofit: Retrofit? = null

    fun getBackendRetrofit(context: Context): Retrofit {
        return backendRetrofit ?: synchronized(this) {
            backendRetrofit ?: buildBackendRetrofit(context.applicationContext).also {
                backendRetrofit = it
            }
        }
    }

    private fun buildBackendRetrofit(appContext: Context): Retrofit {
        val tokenManager = TokenManager(appContext)

        // Klien khusus tanpa Authenticator untuk endpoint /auth/refresh
        val plainClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        val refreshService = Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(plainClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)

        val authenticatedClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager, refreshService))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(authenticatedClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAuthApiService(context: Context): AuthApiService {
        return getBackendRetrofit(context).create(AuthApiService::class.java)
    }
}