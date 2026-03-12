package com.example.adirtkaanki.data.remote

import com.example.adirtkaanki.data.remote.dto.CardMediaUploadResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaApiService {

    @Multipart
    @POST("media/upload/card-assets")
    suspend fun uploadCardAssets(
        @Part frontImage: MultipartBody.Part? = null,
        @Part frontAudio: MultipartBody.Part? = null,
        @Part backImage: MultipartBody.Part? = null,
        @Part backAudio: MultipartBody.Part? = null
    ): CardMediaUploadResponseDto
}
