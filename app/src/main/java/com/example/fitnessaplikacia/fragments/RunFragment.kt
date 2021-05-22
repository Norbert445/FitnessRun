package com.example.fitnessaplikacia.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.services.TrackingService
import com.example.fitnessaplikacia.utility.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_STOP_SERVICE
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.fragment_run.*


class RunFragment : Fragment(R.layout.fragment_run) {

    lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleMapView.onCreate(savedInstanceState)
        googleMapView.getMapAsync {
            googleMap = it
        }

        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)

        fabPauseRun.setOnClickListener {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        fabStopRun.setOnClickListener {
            sendCommandToService(ACTION_STOP_SERVICE)
        }

    }

    private fun sendCommandToService(action: String) {
        requireContext().startService(
            Intent(requireContext(), TrackingService::class.java).apply {
                this.action = action
            }
        )
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        googleMapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        googleMapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        googleMapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        googleMapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        googleMapView.onSaveInstanceState(outState)
    }
}