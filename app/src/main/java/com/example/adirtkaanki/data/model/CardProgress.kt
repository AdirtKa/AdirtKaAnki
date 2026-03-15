package com.example.adirtkaanki.data.model

data class CardProgress(
    val ease: Float,
    val repetitions: Int,
    val intervalDays: Int,
    val lastAnsweredAt: String?,
    val lastAnswerCorrect: Boolean?,
    val dueAt: String
)
