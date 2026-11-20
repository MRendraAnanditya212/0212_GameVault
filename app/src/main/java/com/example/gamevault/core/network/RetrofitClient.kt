package com.example.gamevault.core.network

import com.example.gamevault.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TIMEOUT_SECONDS = 20L

    private val rawgApiKeyInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val apiKey = BuildConfig.RAWG_API_KEY.trim()
        val updatedUrl = if (apiKey.isNotEmpty()) {
            originalRequest.url.newBuilder()
                .addQueryParameter("key", apiKey)
                .build()
        } else {
            originalRequest.url
        }

        val updatedRequest = originalRequest.newBuilder()
            .url(updatedUrl)
            .header("Accept", "application/json")
            .build()

        chain.proceed(updatedRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val rawgOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(rawgApiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private val rawgRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.RAWG_BASE_URL)
            .client(rawgOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val rawgApiService: RawgApiService by lazy {
        rawgRetrofit.create(RawgApiService::class.java)
    }
}