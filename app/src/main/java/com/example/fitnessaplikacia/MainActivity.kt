package com.example.fitnessaplikacia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.ACTION_SHOW_RUN_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Navigate to run fragment when needed
        if(intent?.action == ACTION_SHOW_RUN_FRAGMENT) {
            navHostFragment.findNavController().navigate(
                R.id.action_startRunFragment_to_runFragment
            )
        }

        setSupportActionBar(toolbar)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {
            /* nothing */
        }

        navHostFragment.findNavController().addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id) {
                R.id.setupName,R.id.weightFragment,R.id.runFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }

        }

    }
}