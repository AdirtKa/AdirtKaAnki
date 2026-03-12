package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CardCreateRequest(
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("front_main_text") val frontMainText: String,
    @SerializedName("front_sub_text") val frontSubText: String? = null,
    @SerializedName("back_main_text") val backMainText: String,
    @SerializedName("back_sub_text") val backSubText: String? = null,
    @SerializedName("front_image_id") val frontImageId: String? = null,
    @SerializedName("front_audio_id") val frontAudioId: String? = null,
    @SerializedName("back_image_id") val backImageId: String? = null,
    @SerializedName("back_audio_id") val backAudioId: String? = null
)
