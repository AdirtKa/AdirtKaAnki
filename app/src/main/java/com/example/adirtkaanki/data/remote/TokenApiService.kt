package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.RefreshRequest
import com.example.adirtkaanki.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApiService {

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): TokenResponse
}