package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Чтобы не подставлять access token в сам refresh-запрос
        val isRefreshRequest = originalRequest.url.encodedPath.endsWith("/refresh")

        if (isRefreshRequest) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking {
            sessionManager.getAccessToken()
        }

        val newRequest = if (!accessToken.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}