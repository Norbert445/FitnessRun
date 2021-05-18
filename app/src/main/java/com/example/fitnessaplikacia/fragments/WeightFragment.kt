package com.example.fitnessaplikacia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.fitnessaplikacia.R
import kotlinx.android.synthetic.main.fragment_weight.*

class WeightFragment : Fragment(R.layout.fragment_weight) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        npFirst.maxValue = 150
        npFirst.minValue = 0
        npFirst.value = 80

        npSecond.maxValue = 9
        npSecond.minValue = 0
        npSecond.value = 0
    }
}