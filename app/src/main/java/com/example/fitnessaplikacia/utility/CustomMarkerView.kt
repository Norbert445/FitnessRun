package com.example.fitnessaplikacia.utility

import android.content.Context
import com.example.fitnessaplikacia.models.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.date
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeed}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distance}km"
        tvDistance.text = distanceInKm

        tvDuration.text = TimerUtil.getFormattedTime(run.timeInMillis)

        val caloriesBurned = "${run.calories}kcal"
        tvCaloriesBurned.text = caloriesBurned
    }
}