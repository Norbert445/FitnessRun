package com.example.fitnessaplikacia.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.CustomMarkerView
import com.example.fitnessaplikacia.utility.TimerUtil
import com.example.fitnessaplikacia.utility.ValueFormatter
import com.example.fitnessaplikacia.viewModels.MainViewModel
import com.example.fitnessaplikacia.viewModels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val statisticsViewModel: StatisticsViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setupBarChart()

    }

    private fun setupBarChart() {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisLineColor = Color.WHITE
            textColor =  Color.WHITE
            setDrawGridLines(false)
            setDrawLabels(false)
        }
        barChart.axisLeft.apply {
            valueFormatter = ValueFormatter()
            axisLineColor =  Color.WHITE
            textColor =  Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            valueFormatter = ValueFormatter()
            axisLineColor =  Color.WHITE
            textColor =  Color.WHITE
            setDrawGridLines(false)
            setDrawLabels(false)
        }
        barChart.apply {
            description.text = ""
            legend.isEnabled = false
        }
    }

    private fun setObservers() {
        statisticsViewModel.avgSpeed.observe(viewLifecycleOwner, Observer {
            tvAvgSpeed.text = if (it != null) "${String.format("%.2f", it)} km/h" else "0 km/h"
        })

        statisticsViewModel.totalTime.observe(viewLifecycleOwner, Observer {
            tvTime.text = if (it != null) TimerUtil.getFormattedTime(it) else "00:00:00"
        })

        statisticsViewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            tvDistance.text = if (it != null) "${String.format("%.2f",it)} km" else "0 km"
        })

        statisticsViewModel.caloriesBurned.observe(viewLifecycleOwner, Observer {
            tvCalories.text = if (it != null) "$it kcal" else "0 kcal"
        })
        mainViewModel.runs.observe(viewLifecycleOwner, Observer {
            if(it.size > 1) {
                barChart.visibility = View.VISIBLE
                tvNoRun.visibility = View.GONE
            } else {
                return@Observer
            }
            val allDistances = it.indices.map {i ->
                BarEntry(i.toFloat(), it[i].distance)
            }
            val barDataSet = BarDataSet(allDistances, "Vzdialenosť nedávnych behov").apply {
                valueTextColor = Color.WHITE
                color = ContextCompat.getColor(requireContext(), R.color.secondaryColor)
            }
            barChart.data = BarData(barDataSet)
            barChart.data.setValueFormatter(ValueFormatter())
            barChart.setFitBars(true)
            barChart.marker = CustomMarkerView(it, requireContext(), R.layout.marker_view)
            barChart.invalidate()
        })
    }
}