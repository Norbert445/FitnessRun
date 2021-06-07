package com.example.fitnessaplikacia.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.fitnessaplikacia.utility.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_weight.*
import javax.inject.Inject

@AndroidEntryPoint
class WeightFragment : Fragment(R.layout.fragment_weight) {

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        npFirst.maxValue = 150
        npFirst.minValue = 0
        npFirst.value = 80

        npSecond.maxValue = 9
        npSecond.minValue = 0
        npSecond.value = 0

        btnContinue.setOnClickListener {
            val weight: Float = npFirst.value.toFloat() + (npSecond.value.toFloat() / 10f)

            sharedPref.edit()
                .putFloat(KEY_WEIGHT,weight)
                .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
                .apply()

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.weightFragment,true)
                .build()

            findNavController().navigate(
                R.id.action_weightFragment_to_startRunFragment,
                savedInstanceState,
                navOptions
            )
        }
    }
}