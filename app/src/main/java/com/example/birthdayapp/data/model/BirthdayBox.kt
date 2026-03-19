package com.example.birthdayapp.data.model

data class BirthdayBox(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int? = null,
    val contentRawRes: Int? = null, // For video/audio
    val contentDrawableRes: Int? = null, // For images
    val contentText: String? = null
)