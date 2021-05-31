package com.example.fitnessaplikacia.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessaplikacia.models.Run
import com.example.fitnessaplikacia.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel(){
    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
    
    fun deleteRun(run: Run) = viewModelScope.launch {
        mainRepository.deleteRun(run)
    }

}