package com.example.adirtkaanki.createcard

data class CreateCardUiState(
    val frontMainText: String = "",
    val frontSubText: String = "",
    val backMainText: String = "",
    val backSubText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val frontImageName: String? = null,
    val backImageName: String? = null,
    val frontAudioName: String? = null,
    val backAudioName: String? = null,
    val activeRecordingTarget: RecordingTarget? = null
)
