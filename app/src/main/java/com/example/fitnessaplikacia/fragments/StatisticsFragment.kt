package com.example.fitnessaplikacia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.TimerUtil
import com.example.fitnessaplikacia.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

    }

    private fun setObservers() {
        viewModel.avgSpeed.observe(viewLifecycleOwner, Observer {
            tvAvgSpeed.text = if (it != null) "${String.format("%.2f", it)} km/h" else "0 km/h"
        })

        viewModel.totalTime.observe(viewLifecycleOwner, Observer {
            tvTime.text = if (it != null) TimerUtil.getFormattedTime(it) else "00:00:00"
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            tvDistance.text = if (it != null) "$it km" else "0 km"
        })

        viewModel.caloriesBurned.observe(viewLifecycleOwner, Observer {
            tvCalories.text = if (it != null) "$it kcal" else "0 kcal"
        })
    }
}