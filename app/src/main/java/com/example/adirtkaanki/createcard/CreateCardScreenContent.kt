package com.example.adirtkaanki.createcard

import com.example.adirtkaanki.R
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adirtkaanki.data.remote.ApiConfig
import com.example.adirtkaanki.ui.components.LoadingButton

@Composable
fun CreateCardScreenContent(
    uiState: CreateCardUiState,
    frontImagePreview: Any?,
    backImagePreview: Any?,
    frontAudioPreview: Any?,
    backAudioPreview: Any?,
    onBackClick: () -> Unit,
    onFrontMainTextChange: (String) -> Unit,
    onFrontSubTextChange: (String) -> Unit,
    onBackMainTextChange: (String) -> Unit,
    onBackSubTextChange: (String) -> Unit,
    onPickFrontImageClick: () -> Unit,
    onRemoveFrontImageClick: () -> Unit,
    onPickBackImageClick: () -> Unit,
    onRemoveBackImageClick: () -> Unit,
    onPickFrontAudioClick: () -> Unit,
    onRecordFrontAudioClick: () -> Unit,
    onStopFrontAudioRecordingClick: () -> Unit,
    onRemoveFrontAudioClick: () -> Unit,
    onPickBackAudioClick: () -> Unit,
    onRecordBackAudioClick: () -> Unit,
    onStopBackAudioRecordingClick: () -> Unit,
    onRemoveBackAudioClick: () -> Unit,
    onSaveClick: () -> Unit,
    screenTitle: String = "Create card",
    helperText: String = "Build each side separately, then save the full card.",
    saveButtonText: String = "Create card"
) {
    var selectedSide by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                text = if (selectedSide == 0) "Front" else "Back",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = screenTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = helperText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        SideSwitch(
            selectedSide = selectedSide,
            onFrontClick = { selectedSide = 0 },
            onBackClick = { selectedSide = 1 }
        )

        SideEditorContainer {
            if (selectedSide == 0) {
                SideEditor(
                    title = "Front side",
                    mainText = uiState.frontMainText,
                    subText = uiState.frontSubText,
                    mainTextLabel = "Main text",
                    subTextLabel = "Sub text",
                    onMainTextChange = onFrontMainTextChange,
                    onSubTextChange = onFrontSubTextChange,
                    imageTitle = "Image",
                    imageName = uiState.frontImageName,
                    imagePreview = frontImagePreview,
                    onPickImageClick = onPickFrontImageClick,
                    onRemoveImageClick = onRemoveFrontImageClick,
                    audioTitle = "Audio (.wav)",
                    audioName = uiState.frontAudioName,
                    audioPreview = frontAudioPreview,
                    isRecording = uiState.activeRecordingTarget == RecordingTarget.FRONT_AUDIO,
                    onPickAudioClick = onPickFrontAudioClick,
                    onRecordAudioClick = onRecordFrontAudioClick,
                    onStopAudioRecordingClick = onStopFrontAudioRecordingClick,
                    onRemoveAudioClick = onRemoveFrontAudioClick
                )
            } else {
                SideEditor(
                    title = "Back side",
                    mainText = uiState.backMainText,
                    subText = uiState.backSubText,
                    mainTextLabel = "Main text",
                    subTextLabel = "Sub text",
                    onMainTextChange = onBackMainTextChange,
                    onSubTextChange = onBackSubTextChange,
                    imageTitle = "Image",
                    imageName = uiState.backImageName,
                    imagePreview = backImagePreview,
                    onPickImageClick = onPickBackImageClick,
                    onRemoveImageClick = onRemoveBackImageClick,
                    audioTitle = "Audio (.wav)",
                    audioName = uiState.backAudioName,
                    audioPreview = backAudioPreview,
                    isRecording = uiState.activeRecordingTarget == RecordingTarget.BACK_AUDIO,
                    onPickAudioClick = onPickBackAudioClick,
                    onRecordAudioClick = onRecordBackAudioClick,
                    onStopAudioRecordingClick = onStopBackAudioRecordingClick,
                    onRemoveAudioClick = onRemoveBackAudioClick
                )
            }
        }

        LoadingButton(
            modifier = Modifier.fillMaxWidth(),
            text = saveButtonText,
            isLoading = uiState.isSaving,
            onClick = onSaveClick
        )
    }
}

@Composable
private fun SideSwitch(
    selectedSide: Int,
    onFrontClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SideSwitchButton(
            modifier = Modifier.weight(1f),
            text = "Front",
            selected = selectedSide == 0,
            onClick = onFrontClick
        )
        SideSwitchButton(
            modifier = Modifier.weight(1f),
            text = "Back",
            selected = selectedSide == 1,
            onClick = onBackClick
        )
    }
}

@Composable
private fun SideSwitchButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val textColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f)
    }

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SideEditorContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        content()
    }
}

@Composable
private fun SideEditor(
    title: String,
    mainText: String,
    subText: String,
    mainTextLabel: String,
    subTextLabel: String,
    onMainTextChange: (String) -> Unit,
    onSubTextChange: (String) -> Unit,
    imageTitle: String,
    imageName: String?,
    imagePreview: Any?,
    onPickImageClick: () -> Unit,
    onRemoveImageClick: () -> Unit,
    audioTitle: String,
    audioName: String?,
    audioPreview: Any?,
    isRecording: Boolean,
    onPickAudioClick: () -> Unit,
    onRecordAudioClick: () -> Unit,
    onStopAudioRecordingClick: () -> Unit,
    onRemoveAudioClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        EditorRow(
            label = mainTextLabel,
            field = {
                OutlinedTextField(
                    value = mainText,
                    onValueChange = onMainTextChange,
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter $mainTextLabel") }
                )
            }
        )

        EditorRow(
            label = subTextLabel,
            field = {
                OutlinedTextField(
                    value = subText,
                    onValueChange = onSubTextChange,
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter $subTextLabel") }
                )
            }
        )

        ImageSection(
            title = imageTitle,
            value = imageName,
            imagePreview = imagePreview,
            onPickImageClick = onPickImageClick,
            onRemoveImageClick = onRemoveImageClick
        )

        AudioSection(
            title = audioTitle,
            value = audioName,
            audioPreview = audioPreview,
            isRecording = isRecording,
            onPickAudioClick = onPickAudioClick,
            onRecordAudioClick = onRecordAudioClick,
            onStopAudioRecordingClick = onStopAudioRecordingClick,
            onRemoveAudioClick = onRemoveAudioClick
        )
    }
}

@Composable
private fun EditorRow(
    label: String,
    field: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        field()
    }
}

@Composable
private fun ImageSection(
    title: String,
    value: String?,
    imagePreview: Any?,
    onPickImageClick: () -> Unit,
    onRemoveImageClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value ?: "No file selected",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodyMedium
        )
        imagePreview?.let {
            AsyncImage(
                model = normalizePreviewSource(it),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 280.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BorderActionButton(
                text = "",
                iconRes = R.drawable.ic_gallery,
                onClick = onPickImageClick
            )
            if (value != null) {
                BorderActionButton(
                    text = "",
                    iconRes = R.drawable.ic_trash_bin,
                    onClick = onRemoveImageClick,
                    accentColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AudioSection(
    title: String,
    value: String?,
    audioPreview: Any?,
    isRecording: Boolean,
    onPickAudioClick: () -> Unit,
    onRecordAudioClick: () -> Unit,
    onStopAudioRecordingClick: () -> Unit,
    onRemoveAudioClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value ?: "No file selected",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodyMedium
        )
        audioPreview?.let {
            MediaPreview(audioSource = it)
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BorderActionButton(
                text = "",
                iconRes = R.drawable.ic_wav,
                onClick = onPickAudioClick
            )
            BorderActionButton(
                text = "",
                iconRes = if (isRecording) R.drawable.ic_stop_playback else R.drawable.ic_microphone,
                onClick = if (isRecording) onStopAudioRecordingClick else onRecordAudioClick,
                accentColor = if (isRecording) MaterialTheme.colorScheme.error else null
            )
            if (value != null) {
                BorderActionButton(
                    text = "",
                    iconRes = R.drawable.ic_trash_bin,
                    onClick = onRemoveAudioClick,
                    accentColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BorderActionButton(
    text: String,
    onClick: () -> Unit,
    @DrawableRes iconRes: Int? = null,
    accentColor: Color? = null
) {
    val resolvedColor = accentColor ?: MaterialTheme.colorScheme.onBackground

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            resolvedColor.copy(alpha = if (accentColor == null) 0.25f else 0.45f)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = resolvedColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = resolvedColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (text != "") {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

        }
    }
}

@Composable
private fun MediaPreview(audioSource: Any) {
    val context = LocalContext.current
    var isPlaying by remember(audioSource) { mutableStateOf(false) }
    val mediaPlayer = remember(audioSource) {
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        BorderActionButton(
            text = "",
            iconRes = if (isPlaying) R.drawable.ic_stop_playback else R.drawable.ic_play,
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
                        when (val normalized = normalizePreviewSource(audioSource)) {
                            is Uri -> mediaPlayer.setDataSource(context, normalized)
                            is String -> mediaPlayer.setDataSource(normalized)
                            else -> throw IllegalArgumentException("Unsupported audio source")
                        }
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
        )
    }
}

private fun normalizePreviewSource(source: Any): Any {
    return when (source) {
        is String -> {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                source
            } else {
                ApiConfig.BASE_URL.trimEnd('/') + "/" + source.trimStart('/')
            }
        }
        else -> source
    }
}
