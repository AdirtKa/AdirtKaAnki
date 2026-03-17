package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.session.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private val baseUrl = ApiConfig.BASE_URL
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
    }

    private fun createRefreshClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createRefreshRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createRefreshClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
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
            .baseUrl(baseUrl)
            .client(createMainClient(sessionManager))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun createAuthApiService(sessionManager: SessionManager): AuthApiService {
        return createMainRetrofit(sessionManager).create(AuthApiService::class.java)
    }

    fun createDecksApiService(sessionManager: SessionManager): DecksApiService {
        return createMainRetrofit(sessionManager).create(DecksApiService::class.java)
    }

    fun createCardsApiService(sessionManager: SessionManager): CardsApiService {
        return createMainRetrofit(sessionManager).create(CardsApiService::class.java)
    }

    fun createReviewApiService(sessionManager: SessionManager): ReviewApiService {
        return createMainRetrofit(sessionManager).create(ReviewApiService::class.java)
    }

    fun createStatsApiService(sessionManager: SessionManager): StatsApiService {
        return createMainRetrofit(sessionManager).create(StatsApiService::class.java)
    }

    fun createMediaApiService(sessionManager: SessionManager): MediaApiService {
        return createMainRetrofit(sessionManager).create(MediaApiService::class.java)
    }
}
