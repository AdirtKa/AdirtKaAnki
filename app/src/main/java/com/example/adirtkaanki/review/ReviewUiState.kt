package com.example.adirtkaanki.review

import com.example.adirtkaanki.data.model.ReviewCard

data class ReviewUiState(
    val deckName: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isAnswering: Boolean = false,
    val cards: List<ReviewCard> = emptyList(),
    val errorMessage: String? = null
)
