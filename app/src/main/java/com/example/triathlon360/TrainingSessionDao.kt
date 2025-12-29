package com.example.triathlon360

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrainingSessionDao {

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    suspend fun getAllSessions(): List<TrainingSessionEntity>

    @Insert
    suspend fun insert(session: TrainingSessionEntity)

    @Update
    suspend fun update(session: TrainingSessionEntity)

    @Delete
    suspend fun delete(session: TrainingSessionEntity)
}
