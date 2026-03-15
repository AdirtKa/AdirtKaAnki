package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewAnswerRequest(
    val quality: Int
)

data class CardProgressDto(
    val ease: Float,
    val repetitions: Int,
    @SerializedName("interval_days") val intervalDays: Int,
    @SerializedName("last_answered_at") val lastAnsweredAt: String? = null,
    @SerializedName("last_answer_correct") val lastAnswerCorrect: Boolean? = null,
    @SerializedName("due_at") val dueAt: String
)

data class ReviewCardDto(
    val id: String,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("front_main_text") val frontMainText: String,
    @SerializedName("front_sub_text") val frontSubText: String? = null,
    @SerializedName("back_main_text") val backMainText: String,
    @SerializedName("back_sub_text") val backSubText: String? = null,
    @SerializedName("front_image_id") val frontImageId: String? = null,
    @SerializedName("front_audio_id") val frontAudioId: String? = null,
    @SerializedName("back_image_id") val backImageId: String? = null,
    @SerializedName("back_audio_id") val backAudioId: String? = null,
    @SerializedName("front_image_url") val frontImageUrl: String? = null,
    @SerializedName("front_audio_url") val frontAudioUrl: String? = null,
    @SerializedName("back_image_url") val backImageUrl: String? = null,
    @SerializedName("back_audio_url") val backAudioUrl: String? = null,
    val progress: CardProgressDto
)
