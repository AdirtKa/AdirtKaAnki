package com.example.adirtkaanki.data.repository

import com.example.adirtkaanki.data.remote.MediaApiService
import okhttp3.MultipartBody

class MediaRepository(
    private val api: MediaApiService
) {
    suspend fun uploadCardAssets(
        frontImage: MultipartBody.Part?,
        frontAudio: MultipartBody.Part?,
        backImage: MultipartBody.Part?,
        backAudio: MultipartBody.Part?
    ): Result<CardAssetIds> {
        return try {
            val response = api.uploadCardAssets(
                frontImage = frontImage,
                frontAudio = frontAudio,
                backImage = backImage,
                backAudio = backAudio
            )
            Result.success(
                CardAssetIds(
                    frontImageId = response.frontImage?.id,
                    frontAudioId = response.frontAudio?.id,
                    backImageId = response.backImage?.id,
                    backAudioId = response.backAudio?.id
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
