package com.example.birthdayapp.data.model

data class BirthdayBox(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val accentColor: Long,           // ARGB hex e.g. 0xFFFFB5C8
    val audioResIds: List<Int> = emptyList() // add multiple recordings to shuffle between
)