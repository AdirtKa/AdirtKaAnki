package com.example.adirtkaanki.stats

import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckProgressStats
import com.example.adirtkaanki.data.model.DueForecastPoint
import com.example.adirtkaanki.data.model.ReviewActivityPoint
import com.example.adirtkaanki.data.model.ReviewHistoryPoint
import com.example.adirtkaanki.data.model.UserStatsOverview

data class StatsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val username: String = "",
    val availableDecks: List<Deck> = emptyList(),
    val selectedDeckId: String? = null,
    val selectedDeckName: String? = null,
    val overview: UserStatsOverview? = null,
    val lastReviewActivity: List<ReviewActivityPoint> = emptyList(),
    val reviewHistory: List<ReviewHistoryPoint> = emptyList(),
    val dueForecast: List<DueForecastPoint> = emptyList(),
    val decksProgress: List<DeckProgressStats> = emptyList(),
    val errorMessage: String? = null
)
