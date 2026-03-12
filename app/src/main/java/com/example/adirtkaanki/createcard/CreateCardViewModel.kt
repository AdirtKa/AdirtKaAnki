package com.example.adirtkaanki.createcard

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adirtkaanki.data.remote.ApiFactory
import com.example.adirtkaanki.data.repository.CardsRepository
import com.example.adirtkaanki.data.repository.MediaRepository
import com.example.adirtkaanki.data.session.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateCardViewModel(
    private val deckId: String,
    sessionManager: SessionManager
) : ViewModel() {

    private val cardsApi = ApiFactory.createCardsApiService(sessionManager)
    private val mediaApi = ApiFactory.createMediaApiService(sessionManager)
    private val cardsRepository = CardsRepository(cardsApi)
    private val mediaRepository = MediaRepository(mediaApi)

    var uiState by mutableStateOf(CreateCardUiState())
        private set

    fun onFrontMainTextChange(value: String) {
        uiState = uiState.copy(frontMainText = value)
    }

    fun onFrontSubTextChange(value: String) {
        uiState = uiState.copy(frontSubText = value)
    }

    fun onBackMainTextChange(value: String) {
        uiState = uiState.copy(backMainText = value)
    }

    fun onBackSubTextChange(value: String) {
        uiState = uiState.copy(backSubText = value)
    }

    fun onFrontImageSelected(name: String?) {
        uiState = uiState.copy(frontImageName = name)
    }

    fun onBackImageSelected(name: String?) {
        uiState = uiState.copy(backImageName = name)
    }

    fun onFrontAudioSelected(name: String?) {
        uiState = uiState.copy(frontAudioName = name)
    }

    fun onBackAudioSelected(name: String?) {
        uiState = uiState.copy(backAudioName = name)
    }

    fun onRecordingStateChanged(target: RecordingTarget?) {
        uiState = uiState.copy(activeRecordingTarget = target)
    }

    fun save(
        context: Context,
        frontImageUri: Uri?,
        backImageUri: Uri?,
        frontAudioUri: Uri?,
        backAudioUri: Uri?,
        onSuccess: () -> Unit
    ) {
        val frontMain = uiState.frontMainText.trim()
        val frontSub = uiState.frontSubText.trim().ifBlank { null }
        val backMain = uiState.backMainText.trim()
        val backSub = uiState.backSubText.trim().ifBlank { null }

        if (frontMain.isEmpty() || backMain.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Front and back text are required")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null)

            val frontImagePart = frontImageUri?.let { buildFilePart(context, it, "front_image") }
            val backImagePart = backImageUri?.let { buildFilePart(context, it, "back_image") }
            val frontAudioPart = frontAudioUri?.let { buildFilePart(context, it, "front_audio") }
            val backAudioPart = backAudioUri?.let { buildFilePart(context, it, "back_audio") }

            val uploadResult = if (
                frontImagePart != null ||
                backImagePart != null ||
                frontAudioPart != null ||
                backAudioPart != null
            ) {
                mediaRepository.uploadCardAssets(
                    frontImage = frontImagePart,
                    frontAudio = frontAudioPart,
                    backImage = backImagePart,
                    backAudio = backAudioPart
                )
            } else {
                Result.success(com.example.adirtkaanki.data.repository.CardAssetIds())
            }

            if (uploadResult.isFailure) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = uploadResult.exceptionOrNull()?.message ?: "Failed to upload assets"
                )
                return@launch
            }

            val assetIds = uploadResult.getOrNull() ?: com.example.adirtkaanki.data.repository.CardAssetIds()
            val result = cardsRepository.createCard(
                deckId = deckId,
                frontMainText = frontMain,
                frontSubText = frontSub,
                backMainText = backMain,
                backSubText = backSub,
                frontImageId = assetIds.frontImageId,
                frontAudioId = assetIds.frontAudioId,
                backImageId = assetIds.backImageId,
                backAudioId = assetIds.backAudioId
            )

            if (result.isSuccess) {
                uiState = uiState.copy(isSaving = false)
                onSuccess()
            } else {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to create card"
                )
            }
        }
    }

    private fun buildFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val mimeType = context.contentResolver.getType(uri) ?: defaultMimeTypeFor(partName)
        val fileName = queryDisplayName(context, uri) ?: defaultFileNameFor(partName)
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }

    private fun defaultMimeTypeFor(partName: String): String {
        return if (partName.contains("audio")) "audio/wav" else "application/octet-stream"
    }

    private fun defaultFileNameFor(partName: String): String {
        return if (partName.contains("audio")) "$partName.wav" else "$partName.jpg"
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
}
