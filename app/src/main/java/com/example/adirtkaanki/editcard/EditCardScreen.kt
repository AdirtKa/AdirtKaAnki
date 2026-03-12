package com.example.adirtkaanki.editcard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adirtkaanki.createcard.CreateCardScreenContent
import com.example.adirtkaanki.createcard.RecordingTarget
import com.example.adirtkaanki.createcard.WavAudioRecorder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.util.UUID

@Composable
fun EditCardScreen(
    cardId: String,
    onBackClick: () -> Unit,
    onCardUpdated: () -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val viewModel: EditCardViewModel = viewModel(
        factory = EditCardViewModelFactory(context = context, cardId = cardId)
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
        val uri = file?.let { FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", it) }
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
        if (viewModel.uiState.activeRecordingTarget != null) stopRecording()
        try {
            recorder.start("${target.name.lowercase()}-${UUID.randomUUID()}.wav")
            viewModel.onRecordingStateChanged(target)
        } catch (_: IllegalStateException) {
            Toast.makeText(context, "Failed to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    val frontImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        frontImageUri = uri
        viewModel.onFrontImageSelected(uri?.let { queryDisplayName(context, it) })
    }
    val backImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        backImageUri = uri
        viewModel.onBackImageSelected(uri?.let { queryDisplayName(context, it) })
    }
    val frontAudioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            persistReadPermission(context, uri)
            frontAudioUri = uri
            viewModel.onFrontAudioSelected(queryDisplayName(context, uri))
        }
    }
    val backAudioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            persistReadPermission(context, uri)
            backAudioUri = uri
            viewModel.onBackAudioSelected(queryDisplayName(context, uri))
        }
    }
    val recordPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val target = pendingRecordingTarget
        pendingRecordingTarget = null
        if (granted && target != null) startRecording(target)
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.stop()
            viewModel.onRecordingStateChanged(null)
        }
    }

    CreateCardScreenContent(
        uiState = com.example.adirtkaanki.createcard.CreateCardUiState(
            frontMainText = viewModel.uiState.frontMainText,
            frontSubText = viewModel.uiState.frontSubText,
            backMainText = viewModel.uiState.backMainText,
            backSubText = viewModel.uiState.backSubText,
            isSaving = viewModel.uiState.isSaving,
            errorMessage = viewModel.uiState.errorMessage,
            frontImageName = viewModel.uiState.frontImageName,
            backImageName = viewModel.uiState.backImageName,
            frontAudioName = viewModel.uiState.frontAudioName,
            backAudioName = viewModel.uiState.backAudioName,
            activeRecordingTarget = viewModel.uiState.activeRecordingTarget
        ),
        frontImagePreview = frontImageUri ?: viewModel.uiState.frontImageUrl,
        backImagePreview = backImageUri ?: viewModel.uiState.backImageUrl,
        frontAudioPreview = frontAudioUri ?: viewModel.uiState.frontAudioUrl,
        backAudioPreview = backAudioUri ?: viewModel.uiState.backAudioUrl,
        onBackClick = onBackClick,
        onFrontMainTextChange = viewModel::onFrontMainTextChange,
        onFrontSubTextChange = viewModel::onFrontSubTextChange,
        onBackMainTextChange = viewModel::onBackMainTextChange,
        onBackSubTextChange = viewModel::onBackSubTextChange,
        onPickFrontImageClick = { frontImagePicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onRemoveFrontImageClick = {
            frontImageUri = null
            viewModel.clearFrontImage()
        },
        onPickBackImageClick = { backImagePicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onRemoveBackImageClick = {
            backImageUri = null
            viewModel.clearBackImage()
        },
        onPickFrontAudioClick = { frontAudioPicker.launch(arrayOf("audio/wav", "audio/x-wav", "audio/*")) },
        onRecordFrontAudioClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording(RecordingTarget.FRONT_AUDIO)
            } else {
                pendingRecordingTarget = RecordingTarget.FRONT_AUDIO
                recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStopFrontAudioRecordingClick = ::stopRecording,
        onRemoveFrontAudioClick = {
            frontAudioUri = null
            viewModel.clearFrontAudio()
        },
        onPickBackAudioClick = { backAudioPicker.launch(arrayOf("audio/wav", "audio/x-wav", "audio/*")) },
        onRecordBackAudioClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording(RecordingTarget.BACK_AUDIO)
            } else {
                pendingRecordingTarget = RecordingTarget.BACK_AUDIO
                recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStopBackAudioRecordingClick = ::stopRecording,
        onRemoveBackAudioClick = {
            backAudioUri = null
            viewModel.clearBackAudio()
        },
        onSaveClick = {
            if (viewModel.uiState.activeRecordingTarget != null) stopRecording()
            viewModel.save(
                context = context,
                frontImageUri = frontImageUri,
                backImageUri = backImageUri,
                frontAudioUri = frontAudioUri,
                backAudioUri = backAudioUri,
                onSuccess = onCardUpdated
            )
        },
        screenTitle = "Edit card",
        helperText = "Update the card content and media, then save your changes.",
        saveButtonText = "Save changes"
    )
}

private fun persistReadPermission(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } catch (_: Exception) {
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    if (uri.scheme == "file") return uri.lastPathSegment
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex >= 0 && cursor.moveToFirst()) return cursor.getString(columnIndex)
        }
    return uri.lastPathSegment
}
