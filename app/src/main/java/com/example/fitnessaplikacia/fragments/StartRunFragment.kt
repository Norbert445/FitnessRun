package com.example.fitnessaplikacia.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_start_run.*
import javax.inject.Inject

@AndroidEntryPoint
class StartRunFragment : Fragment(R.layout.fragment_start_run) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGreeting()
    }

    @SuppressLint("SetTextI18n")
    private fun setGreeting() {
        val name = sharedPref.getString(KEY_NAME,"")
        tvGreeting.text = "Ahoj ${name} :)"
    }
}