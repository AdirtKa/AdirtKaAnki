package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

//    private const val BASE_URL = "http://10.0.2.2:8000/"
    private const val BASE_URL = "http://31.59.185.163:8047/"

    fun createAuthApiService(sessionManager: SessionManager): AuthApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val refreshClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val refreshRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(refreshClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tokenApi = refreshRetrofit.create(TokenApiService::class.java)

        val mainClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(TokenAuthenticator(sessionManager, tokenApi))
            .addInterceptor(logging)
            .build()

        val mainRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mainClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return mainRetrofit.create(AuthApiService::class.java)
    }
}