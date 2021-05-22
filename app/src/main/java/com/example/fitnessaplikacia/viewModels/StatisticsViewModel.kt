package com.example.fitnessaplikacia.viewModels

import androidx.lifecycle.ViewModel
import com.example.fitnessaplikacia.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
    val totalTime = mainRepository.getTotalTime()
    val totalDistance = mainRepository.getTotalDistance()
    val avgSpeed = mainRepository.getAvgSpeed()
    val caloriesBurned = mainRepository.getTotalCalories()
}