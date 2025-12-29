package com.example.triathlon360

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String,
    val date: String,
    val durationMin: Int,
    val distanceKm: Double,
    val intensity: String,
    val notes: String
)
