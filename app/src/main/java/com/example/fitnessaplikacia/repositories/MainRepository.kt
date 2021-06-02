package com.example.fitnessaplikacia.repositories

import com.example.fitnessaplikacia.db.RunDao
import com.example.fitnessaplikacia.models.Run
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getRuns() = runDao.getRuns()
    fun getTotalTime() = runDao.getTotalTime()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalCalories() = runDao.getTotalCalories()
    fun getAvgSpeed() = runDao.getAvgSpeed()
}