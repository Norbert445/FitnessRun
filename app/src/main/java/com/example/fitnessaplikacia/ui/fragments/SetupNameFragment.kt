package com.example.fitnessaplikacia.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup_name.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupNameFragment : Fragment(R.layout.fragment_setup_name) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstTime) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupName, true)
                .build()

            findNavController().navigate(
                R.id.action_setupName_to_startRunFragment,
                savedInstanceState,
                navOptions
            )
        }

        btnContinue.setOnClickListener {
            if(check_if_correct_input()) {
                findNavController().navigate(
                    R.id.action_setupName_to_weightFragment,
                )
            } else {
                tiName.error = "Nezadali ste meno"
            }
        }
    }

    private fun check_if_correct_input(): Boolean {
        val name = tiName.text.toString()
        if(name.isEmpty()) {
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME,name)
            .apply()

        return true
    }
}