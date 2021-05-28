package com.example.fitnessaplikacia.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.models.RunEvent
import com.example.fitnessaplikacia.services.Polyline
import com.example.fitnessaplikacia.services.TrackingService
import com.example.fitnessaplikacia.utility.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_STOP_SERVICE
import com.example.fitnessaplikacia.utility.Constants.MAP_ZOOM
import com.example.fitnessaplikacia.utility.Constants.POLYLINE_COLOR
import com.example.fitnessaplikacia.utility.Constants.POLYLINE_WIDTH
import com.example.fitnessaplikacia.utility.TimerUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_run.*


class RunFragment : Fragment(R.layout.fragment_run) {

    lateinit var googleMap: GoogleMap
    private var toggle = true

    private var pathPoints = mutableListOf<Polyline>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleMapView.onCreate(savedInstanceState)
        googleMapView.getMapAsync {
            googleMap = it
            addAllPolylines()

            setObservers()
            setToggleListener()
        }

        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(
            R.id.action_runFragment_to_runsFragment
        )
    }

    private fun showCancelRunDialog() {
        val dialog = MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Zrušiť beh?")
            .setMessage("Chcete zrušiť beh?")
            .setIcon(R.drawable.stop_circle)
            .setPositiveButton("Áno") {_, _ ->
                stopRun()
            }
            .setNegativeButton("Nie") {dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        for(polyLine in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyLine)
            googleMap.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last()[pathPoints.last().size - 1]

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            googleMap.addPolyline(polylineOptions)
        }
    }

    private fun setObservers() {
        TrackingService.runEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                RunEvent.START -> {
                    fabPauseRun.setImageResource(R.drawable.pause)
                }
                RunEvent.PAUSE -> {
                    fabPauseRun.setImageResource(R.drawable.play)
                }
                RunEvent.END -> {

                }
            }
        })

        TrackingService.timeInMillis.observe(viewLifecycleOwner, Observer {
            tvTime.text = TimerUtil.getFormattedTime(it,true)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun setToggleListener() {
        fabPauseRun.setOnClickListener {
            if (toggle) {
                sendCommandToService(ACTION_PAUSE_SERVICE)
                toggle = false
            } else {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                toggle = true
            }
        }
        fabStopRun.setOnClickListener {
            showCancelRunDialog()
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