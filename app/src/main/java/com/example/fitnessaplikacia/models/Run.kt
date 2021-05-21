package com.example.fitnessaplikacia.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "running_table"
)
data class Run(
    var img: Bitmap? = null,
    var date: Long = 0L,
    var timeInMillis: Long = 0L,
    var distance: Float = 0f,
    var avgSpeed: Float = 0f,
    var calories: Int = 0,

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)
