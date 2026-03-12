package com.example.adirtkaanki.createcard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@Composable
fun CreateCardScreen(
    deckId: String,
    onBackClick: () -> Unit,
    onCardCreated: () -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val viewModel: CreateCardViewModel = viewModel(
        factory = CreateCardViewModelFactory(
            context = context,
            deckId = deckId
        )
    )

    val recorder = remember { WavAudioRecorder(appContext) }

    var frontImageUri by remember { mutableStateOf<Uri?>(null) }
    var backImageUri by remember { mutableStateOf<Uri?>(null) }
    var frontAudioUri by remember { mutableStateOf<Uri?>(null) }
    var backAudioUri by remember { mutableStateOf<Uri?>(null) }
    var pendingRecordingTarget by remember { mutableStateOf<RecordingTarget?>(null) }

    fun stopRecording() {
        val target = viewModel.uiState.activeRecordingTarget ?: return
        val file = recorder.stop()
        val uri = file?.let { fileToUri(appContext, it) }
        val name = file?.name

        when (target) {
            RecordingTarget.FRONT_AUDIO -> {
                frontAudioUri = uri
                viewModel.onFrontAudioSelected(name)
            }
            RecordingTarget.BACK_AUDIO -> {
                backAudioUri = uri
                viewModel.onBackAudioSelected(name)
            }
        }

        viewModel.onRecordingStateChanged(null)
    }

    fun startRecording(target: RecordingTarget) {
        if (viewModel.uiState.activeRecordingTarget != null) {
            stopRecording()
        }

        try {
            recorder.start("${target.name.lowercase()}-${UUID.randomUUID()}.wav")
            viewModel.onRecordingStateChanged(target)
        } catch (_: IllegalStateException) {
            Toast.makeText(context, "Failed to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    val frontPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        frontImageUri = uri
        viewModel.onFrontImageSelected(uri?.let { queryDisplayName(context, it) })
    }

    val backPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        backImageUri = uri
        viewModel.onBackImageSelected(uri?.let { queryDisplayName(context, it) })
    }

    val frontAudioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && !isWaveFile(context, uri)) {
            Toast.makeText(context, "Please choose a .wav file", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        if (uri != null) {
            persistReadPermission(context, uri)
        }
        frontAudioUri = uri
        viewModel.onFrontAudioSelected(uri?.let { queryDisplayName(context, it) })
    }

    val backAudioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && !isWaveFile(context, uri)) {
            Toast.makeText(context, "Please choose a .wav file", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        if (uri != null) {
            persistReadPermission(context, uri)
        }
        backAudioUri = uri
        viewModel.onBackAudioSelected(uri?.let { queryDisplayName(context, it) })
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val target = pendingRecordingTarget
        pendingRecordingTarget = null
        if (granted && target != null) {
            startRecording(target)
        } else if (!granted) {
            Toast.makeText(context, "Microphone permission is required to record audio", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.stop()
            viewModel.onRecordingStateChanged(null)
        }
    }

    CreateCardScreenContent(
        uiState = viewModel.uiState,
        frontImageUri = frontImageUri,
        backImageUri = backImageUri,
        frontAudioUri = frontAudioUri,
        backAudioUri = backAudioUri,
        onBackClick = onBackClick,
        onFrontMainTextChange = viewModel::onFrontMainTextChange,
        onFrontSubTextChange = viewModel::onFrontSubTextChange,
        onBackMainTextChange = viewModel::onBackMainTextChange,
        onBackSubTextChange = viewModel::onBackSubTextChange,
        onPickFrontImageClick = {
            frontPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onRemoveFrontImageClick = {
            frontImageUri = null
            viewModel.onFrontImageSelected(null)
        },
        onPickBackImageClick = {
            backPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onRemoveBackImageClick = {
            backImageUri = null
            viewModel.onBackImageSelected(null)
        },
        onPickFrontAudioClick = {
            frontAudioPicker.launch(arrayOf("audio/wav", "audio/x-wav", "audio/*"))
        },
        onRecordFrontAudioClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording(RecordingTarget.FRONT_AUDIO)
            } else {
                pendingRecordingTarget = RecordingTarget.FRONT_AUDIO
                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStopFrontAudioRecordingClick = ::stopRecording,
        onRemoveFrontAudioClick = {
            if (viewModel.uiState.activeRecordingTarget == RecordingTarget.FRONT_AUDIO) {
                stopRecording()
            }
            frontAudioUri = null
            viewModel.onFrontAudioSelected(null)
        },
        onPickBackAudioClick = {
            backAudioPicker.launch(arrayOf("audio/wav", "audio/x-wav", "audio/*"))
        },
        onRecordBackAudioClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording(RecordingTarget.BACK_AUDIO)
            } else {
                pendingRecordingTarget = RecordingTarget.BACK_AUDIO
                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStopBackAudioRecordingClick = ::stopRecording,
        onRemoveBackAudioClick = {
            if (viewModel.uiState.activeRecordingTarget == RecordingTarget.BACK_AUDIO) {
                stopRecording()
            }
            backAudioUri = null
            viewModel.onBackAudioSelected(null)
        },
        onSaveClick = {
            if (viewModel.uiState.activeRecordingTarget != null) {
                stopRecording()
            }
            viewModel.save(
                context = context,
                frontImageUri = frontImageUri,
                backImageUri = backImageUri,
                frontAudioUri = frontAudioUri,
                backAudioUri = backAudioUri,
                onSuccess = onCardCreated
            )
        }
    )
}

private fun persistReadPermission(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: SecurityException) {
    } catch (_: UnsupportedOperationException) {
    }
}

private fun isWaveFile(context: Context, uri: Uri): Boolean {
    val mimeType = context.contentResolver.getType(uri)?.lowercase()
    if (mimeType == "audio/wav" || mimeType == "audio/x-wav" || mimeType == "audio/wave") {
        return true
    }

    val displayName = queryDisplayName(context, uri)?.lowercase()
    return displayName?.endsWith(".wav") == true
}

private fun fileToUri(context: Context, file: java.io.File): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    if (uri.scheme == "file") {
        return uri.lastPathSegment
    }

    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
    return uri.lastPathSegment
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateCardScreenPreview() {
    CreateCardScreenContent(
        uiState = CreateCardUiState(
            frontMainText = "apple",
            frontSubText = "noun",
            backMainText = "yabloko",
            backSubText = "fruit",
            frontImageName = "front.png",
            backImageName = "back.png",
            frontAudioName = "front.wav",
            backAudioName = "back.wav"
        ),
        frontImageUri = null,
        backImageUri = null,
        frontAudioUri = null,
        backAudioUri = null,
        onBackClick = {},
        onFrontMainTextChange = {},
        onFrontSubTextChange = {},
        onBackMainTextChange = {},
        onBackSubTextChange = {},
        onPickFrontImageClick = {},
        onRemoveFrontImageClick = {},
        onPickBackImageClick = {},
        onRemoveBackImageClick = {},
        onPickFrontAudioClick = {},
        onRecordFrontAudioClick = {},
        onStopFrontAudioRecordingClick = {},
        onRemoveFrontAudioClick = {},
        onPickBackAudioClick = {},
        onRecordBackAudioClick = {},
        onStopBackAudioRecordingClick = {},
        onRemoveBackAudioClick = {},
        onSaveClick = {}
    )
}
