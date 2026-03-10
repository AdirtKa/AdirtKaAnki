package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.LoginRequest
import com.example.adirtkaanki.data.remote.dto.MeResponse
import com.example.adirtkaanki.data.remote.dto.RefreshRequest
import com.example.adirtkaanki.data.remote.dto.RegisterRequest
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val AUTH_PREFIX = "auth/"

interface AuthApiService {

    @POST("${AUTH_PREFIX}login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("${AUTH_PREFIX}register")
    suspend fun register(
        @Body request: RegisterRequest
    ): TokenResponse

    @GET("${AUTH_PREFIX}me")
    suspend fun me(): MeResponse
}