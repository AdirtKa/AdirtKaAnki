package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.remote.AuthApiService
import com.example.adirtkaanki.data.remote.dto.LoginRequest
import com.example.adirtkaanki.data.remote.dto.MeResponse
import com.example.adirtkaanki.data.remote.dto.RegisterRequest
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import com.example.adirtkaanki.data.session.SessionManager

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<TokenResponse> {
        return try {
            val response = api.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            sessionManager.saveSession(response.accessToken, response.refreshToken)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String): Result<TokenResponse> {
        return try {
            val response = api.register(
                RegisterRequest(
                    username = username,
                    password = password
                )
            )

            sessionManager.saveSession(response.accessToken, response.refreshToken)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<MeResponse> {
        return try {
            val response = api.me()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
