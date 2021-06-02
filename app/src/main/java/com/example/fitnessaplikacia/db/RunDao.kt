package com.example.fitnessaplikacia.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fitnessaplikacia.models.Run

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table")
    fun getRuns(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(calories) FROM running_table")
    fun getTotalCalories(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM running_table")
    fun getTotalDistance(): LiveData<Float>

    @Query("SELECT AVG(avgSpeed) FROM running_table")
    fun getAvgSpeed(): LiveData<Float>
}