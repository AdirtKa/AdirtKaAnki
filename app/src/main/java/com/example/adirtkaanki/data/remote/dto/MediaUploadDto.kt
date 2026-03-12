package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MediaUploadDto(
    val id: String,
    val filename: String? = null,
    @SerializedName("content_type") val contentType: String? = null,
    val url: String? = null
)
