package com.example.triathlon360

data class TrainingSession(
    val type: String,          // "swim", "bike", "run"
    val date: String,          // "2025-12-16"
    val durationMin: Int,      // 45
    val distance: Float,       // 1500 (m) or 20 (km) depending on your app
    val intensity: String = "EASY", // EASY / MOD / HARD
    val notes: String = ""
)
