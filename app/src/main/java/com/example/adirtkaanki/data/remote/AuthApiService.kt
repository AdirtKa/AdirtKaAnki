package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.LoginRequest
import com.example.adirtkaanki.data.remote.dto.MeResponse
import com.example.adirtkaanki.data.remote.dto.RefreshRequest
import com.example.adirtkaanki.data.remote.dto.RegisterRequest
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): TokenResponse

    @GET("me")
    suspend fun me(): MeResponse
}