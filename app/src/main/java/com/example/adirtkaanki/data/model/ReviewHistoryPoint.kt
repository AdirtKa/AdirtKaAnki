package com.example.adirtkaanki.data.model

data class ReviewHistoryPoint(
    val date: String,
    val totalReviews: Int,
    val correctReviews: Int,
    val incorrectReviews: Int,
    val averageQuality: Float?
)

