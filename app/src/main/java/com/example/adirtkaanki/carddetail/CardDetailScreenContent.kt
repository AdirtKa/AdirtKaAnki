package com.example.adirtkaanki.carddetail

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adirtkaanki.data.remote.ApiConfig
import kotlin.math.abs

@Composable
fun CardDetailScreenContent(
    uiState: CardDetailUiState,
    onBackClick: () -> Unit
) {
    var showFront by remember(uiState.card?.id) { mutableStateOf(true) }
    var dragAmount by remember(uiState.card?.id) { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("Back to cards")
            }

            Text(
                text = if (showFront) "Front" else "Back",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Card details",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        when {
            uiState.isLoading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            uiState.card != null -> {
                Text(
                    text = "Tap or swipe the container to switch sides.",
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
                        .pointerInput(uiState.card.id, showFront) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, drag ->
                                    dragAmount += drag
                                },
                                onDragEnd = {
                                    if (abs(dragAmount) > 40f) {
                                        showFront = !showFront
                                    }
                                    dragAmount = 0f
                                },
                                onDragCancel = {
                                    dragAmount = 0f
                                }
                            )
                        }
                        .padding(18.dp)
                ) {
                    if (showFront) {
                        CardSideContent(
                            sideLabel = "Front",
                            mainText = uiState.card.frontMainText,
                            subText = uiState.card.frontSubText,
                            imageUrl = uiState.card.frontImageUrl,
                            audioUrl = uiState.card.frontAudioUrl,
                            imageDescription = "Front image",
                            audioLabel = "Front audio"
                        )
                    } else {
                        CardSideContent(
                            sideLabel = "Back",
                            mainText = uiState.card.backMainText,
                            subText = uiState.card.backSubText,
                            imageUrl = uiState.card.backImageUrl,
                            audioUrl = uiState.card.backAudioUrl,
                            imageDescription = "Back image",
                            audioLabel = "Back audio"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSideContent(
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

        DetailRow(
            label = "Main text",
            value = mainText,
            emphasized = true
        )

        subText?.takeIf { it.isNotBlank() }?.let {
            DetailRow(
                label = "Sub text",
                value = it
            )
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
                AudioPlayer(url = toAbsoluteMediaUrl(it))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    emphasized: Boolean = false
) {
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
private fun AudioPlayer(url: String) {
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
        onDispose {
            mediaPlayer.release()
        }
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
        }
    ) {
        Text(if (isPlaying) "Stop" else "Play")
    }
}

private fun toAbsoluteMediaUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        ApiConfig.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
    }
}
