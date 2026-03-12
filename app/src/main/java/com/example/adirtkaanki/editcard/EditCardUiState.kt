package com.example.adirtkaanki.editcard

import com.example.adirtkaanki.createcard.RecordingTarget

data class EditCardUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val frontMainText: String = "",
    val frontSubText: String = "",
    val backMainText: String = "",
    val backSubText: String = "",
    val frontImageName: String? = null,
    val backImageName: String? = null,
    val frontAudioName: String? = null,
    val backAudioName: String? = null,
    val frontImageId: String? = null,
    val backImageId: String? = null,
    val frontAudioId: String? = null,
    val backAudioId: String? = null,
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val frontAudioUrl: String? = null,
    val backAudioUrl: String? = null,
    val activeRecordingTarget: RecordingTarget? = null
)
