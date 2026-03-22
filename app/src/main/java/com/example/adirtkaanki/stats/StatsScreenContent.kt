package com.example.adirtkaanki.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.adirtkaanki.data.model.Deck
import com.example.adirtkaanki.data.model.DeckProgressStats
import com.example.adirtkaanki.data.model.DueForecastPoint
import com.example.adirtkaanki.data.model.ReviewActivityPoint
import com.example.adirtkaanki.data.model.ReviewHistoryPoint
import com.example.adirtkaanki.data.model.UserStatsOverview
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

private enum class StatsSection(val title: String) {
    OVERVIEW("Overview"),
    HEATMAP("Heatmap"),
    ACTIVITY("Activity"),
    HISTORY("History"),
    FORECAST("Forecast"),
    DECKS("Decks")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreenContent(
    uiState: StatsUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onDeckSelected: (Deck?) -> Unit
) {
    var selectedSection by rememberSaveable { mutableStateOf(StatsSection.OVERVIEW) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("Back to decks")
            }
            Text(
                text = uiState.selectedDeckName ?: "All decks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (uiState.username.isBlank()) "Review and learning metrics" else "Review and learning metrics for ${uiState.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.74f)
        )

        DeckFilterRow(
            decks = uiState.availableDecks,
            selectedDeckId = uiState.selectedDeckId,
            onDeckSelected = onDeckSelected
        )

        SectionRow(
            selectedSection = selectedSection,
            onSectionSelected = { selectedSection = it }
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        uiState.errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        when (selectedSection) {
                            StatsSection.OVERVIEW -> {
                                uiState.overview?.let { OverviewSection(it) } ?: EmptySection("No overview data yet")
                            }
                            StatsSection.HEATMAP -> {
                                ChartSection(
                                    title = "Review heatmap",
                                    subtitle = "GitHub-style daily review intensity from review history"
                                ) {
                                    ReviewHeatmap(points = uiState.reviewHistory)
                                }
                            }
                            StatsSection.ACTIVITY -> {
                                ChartSection(
                                    title = "Last review activity",
                                    subtitle = "Snapshot by last_answered_at per day"
                                ) {
                                    ActivityChart(points = uiState.lastReviewActivity)
                                }
                            }
                            StatsSection.HISTORY -> {
                                ChartSection(
                                    title = "Review history",
                                    subtitle = "Real review events over the last 30 days"
                                ) {
                                    HistoryChart(points = uiState.reviewHistory)
                                }
                            }
                            StatsSection.FORECAST -> {
                                ChartSection(
                                    title = "Due forecast",
                                    subtitle = "Cards expected to become due over the next 14 days"
                                ) {
                                    ForecastChart(points = uiState.dueForecast)
                                }
                            }
                            StatsSection.DECKS -> {
                                ChartSection(
                                    title = "Deck progress",
                                    subtitle = "Cross-deck comparison for your account"
                                ) {
                                    DeckProgressSection(items = uiState.decksProgress)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionRow(
    selectedSection: StatsSection,
    onSectionSelected: (StatsSection) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatsSection.entries.forEach { section ->
            FilterChip(
                label = section.title,
                selected = selectedSection == section,
                onClick = { onSectionSelected(section) }
            )
        }
    }
}

@Composable
private fun DeckFilterRow(
    decks: List<Deck>,
    selectedDeckId: String?,
    onDeckSelected: (Deck?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "All decks",
            selected = selectedDeckId == null,
            onClick = { onDeckSelected(null) }
        )
        decks.forEach { deck ->
            FilterChip(
                label = deck.name,
                selected = selectedDeckId == deck.id,
                onClick = { onDeckSelected(deck) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = if (selected) 0.55f else 0.35f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OverviewSection(overview: UserStatsOverview) {
    ChartSection(
        title = "Overview",
        subtitle = "Current learning state for the selected scope"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricChip(Modifier.weight(1f), "Decks", overview.deckCount.toString(), MaterialTheme.colorScheme.primary)
                MetricChip(Modifier.weight(1f), "Cards", overview.cardCount.toString(), MaterialTheme.colorScheme.tertiary)
                MetricChip(Modifier.weight(1f), "Due", overview.dueNow.toString(), MaterialTheme.colorScheme.error)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricChip(Modifier.weight(1f), "New", overview.newCards.toString(), MaterialTheme.colorScheme.tertiary)
                MetricChip(Modifier.weight(1f), "Learning", overview.learningCards.toString(), MaterialTheme.colorScheme.primary)
                MetricChip(Modifier.weight(1f), "Mastered", overview.masteredCards.toString(), MaterialTheme.colorScheme.secondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MetricChip(Modifier.weight(1f), "Reviewed", overview.reviewedCards.toString(), MaterialTheme.colorScheme.primary)
                MetricChip(Modifier.weight(1f), "Correct", overview.correctCards.toString(), MaterialTheme.colorScheme.secondary)
                MetricChip(Modifier.weight(1f), "Incorrect", overview.incorrectCards.toString(), MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MetricChip(modifier: Modifier, label: String, value: String, accent: Color) {
    Column(
        modifier = modifier
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = accent, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
    }
}

@Composable
private fun ChartSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.74f))
        content()
    }
}

@Composable
private fun EmptySection(text: String) {
    ChartSection(
        title = "Overview",
        subtitle = "Current learning state for the selected scope"
    ) {
        EmptyChartText(text)
    }
}

@Composable
private fun ReviewHeatmap(points: List<ReviewHistoryPoint>) {
    if (points.isEmpty()) {
        EmptyChartText()
        return
    }

    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val today = LocalDate.now()
    val startDate = today.minusDays(83)
    val valuesByDate = points.associate { LocalDate.parse(it.date, formatter) to it.totalReviews }
    val days = generateSequence(startDate) { current ->
        current.plusDays(1).takeIf { !it.isAfter(today) }
    }.toList()

    val paddedStart = startDate.minusDays(startDate.dayOfWeek.value.toLong() - 1L)
    val paddedEnd = today.plusDays((DayOfWeek.SUNDAY.value - today.dayOfWeek.value).toLong())
    val fullDays = generateSequence(paddedStart) { current ->
        current.plusDays(1).takeIf { !it.isAfter(paddedEnd) }
    }.toList()
    val weeks = fullDays.chunked(7)
    val maxReviews = max(1, valuesByDate.values.maxOrNull() ?: 1)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            (0..4).forEach { level ->
                HeatmapLegendCell(level = level)
            }
            Text(
                text = "More",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { label ->
                    Box(
                        modifier = Modifier.height(14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                weeks.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        week.forEach { date ->
                            val value = valuesByDate[date] ?: 0
                            val level = when {
                                value <= 0 -> 0
                                value >= maxReviews -> 4
                                value >= maxReviews * 0.66f -> 3
                                value >= maxReviews * 0.33f -> 2
                                else -> 1
                            }
                            HeatmapCell(
                                level = level,
                                inRange = date in days,
                                label = date.toString(),
                                value = value
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapLegendCell(level: Int) {
    HeatmapCell(level = level, inRange = true, label = "legend", value = level)
}

@Composable
private fun HeatmapCell(level: Int, inRange: Boolean, label: String, value: Int) {
    val base = MaterialTheme.colorScheme.primary
    val fill = when {
        !inRange -> Color.Transparent
        level <= 0 -> MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        level == 1 -> base.copy(alpha = 0.28f)
        level == 2 -> base.copy(alpha = 0.45f)
        level == 3 -> base.copy(alpha = 0.7f)
        else -> base
    }

    Box(
        modifier = Modifier
            .width(14.dp)
            .height(14.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .background(fill, RoundedCornerShape(4.dp))
    )
}

@Composable
private fun ActivityChart(points: List<ReviewActivityPoint>) {
    if (points.isEmpty()) {
        EmptyChartText()
        return
    }
    val maxValue = max(1, points.maxOf { it.total })
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        points.takeLast(10).forEach { point ->
            TripleBarRow(
                label = point.date.takeLast(5),
                primaryValue = point.total,
                secondaryValue = point.correct,
                tertiaryValue = point.incorrect,
                maxValue = maxValue,
                primaryColor = MaterialTheme.colorScheme.primary,
                secondaryColor = MaterialTheme.colorScheme.secondary,
                tertiaryColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun HistoryChart(points: List<ReviewHistoryPoint>) {
    if (points.isEmpty()) {
        EmptyChartText()
        return
    }
    val maxValue = max(1, points.maxOf { it.totalReviews })
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        points.takeLast(10).forEach { point ->
            TripleBarRow(
                label = point.date.takeLast(5),
                primaryValue = point.totalReviews,
                secondaryValue = point.correctReviews,
                tertiaryValue = point.incorrectReviews,
                maxValue = maxValue,
                primaryColor = MaterialTheme.colorScheme.primary,
                secondaryColor = MaterialTheme.colorScheme.secondary,
                tertiaryColor = MaterialTheme.colorScheme.error,
                trailing = point.averageQuality?.let { "q ${"%.1f".format(it)}" } ?: "q -"
            )
        }
    }
}

@Composable
private fun ForecastChart(points: List<DueForecastPoint>) {
    if (points.isEmpty()) {
        EmptyChartText()
        return
    }
    val maxValue = max(1, points.maxOf { it.dueCards })
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        points.forEach { point ->
            SingleBarRow(
                label = point.date.takeLast(5),
                value = point.dueCards,
                maxValue = maxValue,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun DeckProgressSection(items: List<DeckProgressStats>) {
    if (items.isEmpty()) {
        EmptyChartText()
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = item.deckName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricChip(Modifier.weight(1f), "Total", item.totalCards.toString(), MaterialTheme.colorScheme.primary)
                    MetricChip(Modifier.weight(1f), "New", item.newCards.toString(), MaterialTheme.colorScheme.tertiary)
                    MetricChip(Modifier.weight(1f), "Due", item.dueCards.toString(), MaterialTheme.colorScheme.error)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricChip(Modifier.weight(1f), "Learning", item.learningCards.toString(), MaterialTheme.colorScheme.primary)
                    MetricChip(Modifier.weight(1f), "Mastered", item.masteredCards.toString(), MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
private fun EmptyChartText(text: String = "No data yet") {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )
}

@Composable
private fun SingleBarRow(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.width(42.dp), color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyMedium)
        Box(modifier = Modifier.weight(1f).height(10.dp).border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.toFloat() / maxValue.toFloat())
                    .height(10.dp)
                    .background(color, RoundedCornerShape(8.dp))
            )
        }
        Text(text = value.toString(), color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TripleBarRow(
    label: String,
    primaryValue: Int,
    secondaryValue: Int,
    tertiaryValue: Int,
    maxValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    trailing: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(text = trailing ?: "$primaryValue total", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.74f))
        }
        SingleBarRow(label = "T", value = primaryValue, maxValue = maxValue, color = primaryColor)
        SingleBarRow(label = "C", value = secondaryValue, maxValue = maxValue, color = secondaryColor)
        SingleBarRow(label = "I", value = tertiaryValue, maxValue = maxValue, color = tertiaryColor)
    }
}
