package com.example.adirtkaanki.data.remote.dto

data class CardDto(
    val id: String,
    val front: String? = null,
    val back: String? = null,
    val question: String? = null,
    val answer: String? = null,
    val term: String? = null,
    val definition: String? = null
)
