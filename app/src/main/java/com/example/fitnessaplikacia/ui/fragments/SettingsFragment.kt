package com.example.fitnessaplikacia.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import com.example.fitnessaplikacia.utility.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load_from_shared_pref()

        btnChange.setOnClickListener {
            if (check_if_correct_input()) {
                view.hideKeyboard()
                Snackbar.make(view,"Zmena údajov bola úspešná",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun load_from_shared_pref() {
        val name = sharedPref.getString(KEY_NAME, "").toString()
        val weight = sharedPref.getFloat(KEY_WEIGHT, 85f).toString()

        tiName.setText(name)
        tiWeight.setText(weight)
    }

    private fun check_if_correct_input(): Boolean {
        val name = tiName.text.toString()
        val weight = tiWeight.text.toString()
        var valid = true

        if (name.isEmpty()) {
            tiName.setError("Nezadali ste meno")
            valid = false
        }
        if (weight.isEmpty()) {
            tiWeight.setError("Nezadali ste vahu")
            valid = false
        }

        if (!valid) return false

        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .apply()

        return true
    }
}