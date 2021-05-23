package com.example.fitnessaplikacia.models

sealed class RunEvent {
    object START: RunEvent()
    object END: RunEvent()
    object PAUSE: RunEvent()
}
