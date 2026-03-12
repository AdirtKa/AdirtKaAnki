package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CardMediaUploadResponseDto(
    @SerializedName("front_image") val frontImage: MediaUploadDto? = null,
    @SerializedName("front_audio") val frontAudio: MediaUploadDto? = null,
    @SerializedName("back_image") val backImage: MediaUploadDto? = null,
    @SerializedName("back_audio") val backAudio: MediaUploadDto? = null
)
