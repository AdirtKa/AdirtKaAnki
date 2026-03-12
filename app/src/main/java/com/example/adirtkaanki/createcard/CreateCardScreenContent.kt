package com.example.adirtkaanki.createcard

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.net.Uri
import android.widget.Toast
import com.example.adirtkaanki.ui.components.LoadingButton

@Composable
fun CreateCardScreenContent(
    uiState: CreateCardUiState,
    frontImageUri: Uri?,
    backImageUri: Uri?,
    frontAudioUri: Uri?,
    backAudioUri: Uri?,
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
    onSaveClick: () -> Unit
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
            text = "Create card",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Build each side separately, then save the full card.",
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

        TabRow(selectedTabIndex = selectedSide) {
            Tab(
                selected = selectedSide == 0,
                onClick = { selectedSide = 0 },
                text = { Text("Front") }
            )
            Tab(
                selected = selectedSide == 1,
                onClick = { selectedSide = 1 },
                text = { Text("Back") }
            )
        }

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
                    imageUri = frontImageUri,
                    onPickImageClick = onPickFrontImageClick,
                    onRemoveImageClick = onRemoveFrontImageClick,
                    audioTitle = "Audio (.wav)",
                    audioName = uiState.frontAudioName,
                    audioUri = frontAudioUri,
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
                    imageUri = backImageUri,
                    onPickImageClick = onPickBackImageClick,
                    onRemoveImageClick = onRemoveBackImageClick,
                    audioTitle = "Audio (.wav)",
                    audioName = uiState.backAudioName,
                    audioUri = backAudioUri,
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
            text = "Create card",
            isLoading = uiState.isSaving,
            onClick = onSaveClick
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
    imageUri: Uri?,
    onPickImageClick: () -> Unit,
    onRemoveImageClick: () -> Unit,
    audioTitle: String,
    audioName: String?,
    audioUri: Uri?,
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
            imageUri = imageUri,
            onPickImageClick = onPickImageClick,
            onRemoveImageClick = onRemoveImageClick
        )

        AudioSection(
            title = audioTitle,
            value = audioName,
            audioUri = audioUri,
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
    imageUri: Uri?,
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
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 280.dp),
                contentScale = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onPickImageClick) {
                Text("Choose from gallery")
            }
            if (value != null) {
                TextButton(onClick = onRemoveImageClick) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
private fun AudioSection(
    title: String,
    value: String?,
    audioUri: Uri?,
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
        audioUri?.let {
            LocalAudioPreview(audioUri = it)
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onPickAudioClick) {
                Text("Choose file")
            }
            Button(onClick = if (isRecording) onStopAudioRecordingClick else onRecordAudioClick) {
                Text(if (isRecording) "Stop recording" else "Record")
            }
            if (value != null) {
                TextButton(onClick = onRemoveAudioClick) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
private fun LocalAudioPreview(audioUri: Uri) {
    val context = LocalContext.current
    var isPlaying by remember(audioUri) { mutableStateOf(false) }
    val mediaPlayer = remember(audioUri) {
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
                        mediaPlayer.setDataSource(context, audioUri)
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
}
