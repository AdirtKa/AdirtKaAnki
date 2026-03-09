package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.Database
import com.example.adirtkaanki.data.remote.AuthApiService
import com.example.adirtkaanki.data.remote.dto.LoginRequest
import com.example.adirtkaanki.data.remote.dto.MeResponse
import com.example.adirtkaanki.data.remote.dto.RegisterRequest
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import com.example.adirtkaanki.data.session.SessionManager


sealed class LoginResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String
    ) : LoginResult()

    data class Error(
        val message: String
    ) : LoginResult()
}

class AuthRepository(
    private val database: Database,
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
