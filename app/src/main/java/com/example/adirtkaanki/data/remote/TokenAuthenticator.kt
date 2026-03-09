package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.RefreshRequest
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val tokenApi: TokenApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Не уходим в бесконечный цикл
        if (responseCount(response) >= 2) {
            return null
        }

        return runBlocking {
            val refreshToken = sessionManager.getRefreshToken()

            if (refreshToken.isNullOrBlank()) {
                sessionManager.clearSession()
                return@runBlocking null
            }

            try {
                val tokenResponse = tokenApi.refresh(
                    RefreshRequest(refreshToken = refreshToken)
                )

                sessionManager.saveSession(
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken
                )

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                    .build()
            } catch (e: Exception) {
                sessionManager.clearSession()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}