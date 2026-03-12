package com.example.adirtkaanki.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeckCardStatsDto(
    @SerializedName("not_studied") val notStudied: Int,
    @SerializedName("answered_correctly") val answeredCorrectly: Int,
    @SerializedName("answered_incorrectly") val answeredIncorrectly: Int
)
