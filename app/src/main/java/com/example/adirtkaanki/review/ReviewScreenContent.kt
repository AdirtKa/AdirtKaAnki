package com.example.adirtkaanki.review

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adirtkaanki.data.model.ReviewCard
import com.example.adirtkaanki.data.remote.ApiConfig
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreenContent(
    uiState: ReviewUiState,
    onBackClick: () -> Unit,
    onViewCardsClick: () -> Unit,
    onRefresh: () -> Unit,
    onAnswer: (String, Int) -> Unit
) {
    val currentCard = uiState.cards.firstOrNull()
    var showFront by remember(currentCard?.id) { mutableStateOf(true) }
    var dragAmount by remember(currentCard?.id) { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                text = if (showFront) "Front" else "Back",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = uiState.deckName.ifBlank { "Review" },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryChip(
                modifier = Modifier.weight(1f),
                label = "Due now",
                value = uiState.cards.size.toString(),
                accentColor = MaterialTheme.colorScheme.primary
            )
            SummaryChip(
                modifier = Modifier.weight(1f),
                label = "Mode",
                value = "Review",
                accentColor = MaterialTheme.colorScheme.tertiary
            )
        }

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

                uiState.errorMessage != null && currentCard == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                currentCard == null -> {
                    EmptyReviewState(onViewCardsClick = onViewCardsClick)
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

                        Text(
                            text = "Tap or swipe the card to switch sides.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { showFront = !showFront }
                                .pointerInput(currentCard.id, showFront) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, drag -> dragAmount += drag },
                                        onDragEnd = {
                                            if (abs(dragAmount) > 40f) showFront = !showFront
                                            dragAmount = 0f
                                        },
                                        onDragCancel = { dragAmount = 0f }
                                    )
                                }
                                .padding(18.dp)
                        ) {
                            if (showFront) {
                                ReviewCardSide(
                                    sideLabel = "Front",
                                    mainText = currentCard.frontMainText,
                                    subText = currentCard.frontSubText,
                                    imageUrl = currentCard.frontImageUrl,
                                    audioUrl = currentCard.frontAudioUrl,
                                    imageDescription = "Front image",
                                    audioLabel = "Front audio"
                                )
                            } else {
                                ReviewCardSide(
                                    sideLabel = "Back",
                                    mainText = currentCard.backMainText,
                                    subText = currentCard.backSubText,
                                    imageUrl = currentCard.backImageUrl,
                                    audioUrl = currentCard.backAudioUrl,
                                    imageDescription = "Back image",
                                    audioLabel = "Back audio"
                                )
                            }
                        }

                        ProgressBlock(card = currentCard)

                        Text(
                            text = "Rate recall quality",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AnswerButtonRow(
                                answers = listOf(
                                    ReviewAnswerOption(0, "0 Again", MaterialTheme.colorScheme.error),
                                    ReviewAnswerOption(1, "1 Hard", MaterialTheme.colorScheme.error.copy(alpha = 0.8f)),
                                    ReviewAnswerOption(2, "2 OK", MaterialTheme.colorScheme.tertiary)
                                ),
                                enabled = !uiState.isAnswering,
                                onAnswer = { onAnswer(currentCard.id, it) }
                            )
                            AnswerButtonRow(
                                answers = listOf(
                                    ReviewAnswerOption(3, "3 Good", MaterialTheme.colorScheme.primary),
                                    ReviewAnswerOption(4, "4 Easy", MaterialTheme.colorScheme.secondary)
                                ),
                                enabled = !uiState.isAnswering,
                                onAnswer = { onAnswer(currentCard.id, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ReviewAnswerOption(
    val quality: Int,
    val label: String,
    val accentColor: Color
)

@Composable
private fun EmptyReviewState(onViewCardsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No cards are due right now",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "You can return later or open the deck card list to manage its content.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        Button(
            onClick = onViewCardsClick,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("View cards")
        }
    }
}

@Composable
private fun SummaryChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ReviewCardSide(
    sideLabel: String,
    mainText: String,
    subText: String?,
    imageUrl: String?,
    audioUrl: String?,
    imageDescription: String,
    audioLabel: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = sideLabel,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        ReviewDetailRow(label = "Main text", value = mainText, emphasized = true)

        subText?.takeIf { it.isNotBlank() }?.let {
            ReviewDetailRow(label = "Sub text", value = it)
        }

        imageUrl?.let {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Text(
                text = "Image",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = toAbsoluteMediaUrl(it),
                contentDescription = imageDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 360.dp),
                contentScale = ContentScale.Fit
            )
        }

        audioUrl?.let {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = audioLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                ReviewAudioPlayer(url = toAbsoluteMediaUrl(it))
            }
        }
    }
}

@Composable
private fun ReviewDetailRow(label: String, value: String, emphasized: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "$label -",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.35f)
            )
            Text(
                text = value,
                style = if (emphasized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(0.65f)
            )
        }
    }
}

@Composable
private fun ProgressBlock(card: ReviewCard) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        ReviewDetailRow(label = "Repetitions", value = card.progress.repetitions.toString())
        ReviewDetailRow(label = "Interval days", value = card.progress.intervalDays.toString())
        ReviewDetailRow(label = "Ease", value = card.progress.ease.toString())
        ReviewDetailRow(
            label = "Last result",
            value = when (card.progress.lastAnswerCorrect) {
                true -> "Correct"
                false -> "Incorrect"
                null -> "New"
            }
        )
    }
}

@Composable
private fun AnswerButtonRow(
    answers: List<ReviewAnswerOption>,
    enabled: Boolean,
    onAnswer: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        answers.forEach { answer ->
            Button(
                onClick = { onAnswer(answer.quality) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    answer.accentColor.copy(alpha = 0.45f)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    contentColor = answer.accentColor,
                    disabledContentColor = answer.accentColor.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = answer.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ReviewAudioPlayer(url: String) {
    val context = LocalContext.current
    var isPlaying by remember(url) { mutableStateOf(false) }
    val mediaPlayer = remember(url) {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnCompletionListener { isPlaying = false }
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose { mediaPlayer.release() }
    }

    Button(
        onClick = {
            if (isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                isPlaying = false
            } else {
                try {
                    mediaPlayer.reset()
                    mediaPlayer.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.setOnCompletionListener { isPlaying = false }
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    isPlaying = true
                } catch (_: Exception) {
                    isPlaying = false
                    Toast.makeText(context, "Failed to play audio", Toast.LENGTH_SHORT).show()
                }
            }
        },
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(if (isPlaying) "Stop" else "Play")
    }
}

private fun toAbsoluteMediaUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url
    else ApiConfig.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
}
