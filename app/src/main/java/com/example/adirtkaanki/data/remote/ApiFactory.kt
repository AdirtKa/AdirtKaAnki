package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private const val BASE_URL = "http://10.0.2.2:8047/"
    //private const val BASE_URL = "http://31.59.185.163:8047/"

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun createRefreshClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createRefreshRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createRefreshClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createTokenApiService(): TokenApiService {
        return createRefreshRetrofit().create(TokenApiService::class.java)
    }

    private fun createMainClient(sessionManager: SessionManager): OkHttpClient {
        val tokenApi = createTokenApiService()

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(TokenAuthenticator(sessionManager, tokenApi))
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createMainRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createMainClient(sessionManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createAuthApiService(sessionManager: SessionManager): AuthApiService {
        return createMainRetrofit(sessionManager).create(AuthApiService::class.java)
    }

    fun createDecksApiService(sessionManager: SessionManager): DecksApiService {
        return createMainRetrofit(sessionManager).create(DecksApiService::class.java)
    }
}